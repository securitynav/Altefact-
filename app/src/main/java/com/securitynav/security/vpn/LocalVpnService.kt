package com.securitynav.security.vpn

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.isActive
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.securitynav.security.R
import com.securitynav.security.monitor.NetworkMonitor
import com.securitynav.security.data.db.SecurityDatabaseHelper
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class LocalVpnService : VpnService() {

    private val TAG = "LocalVpnService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var vpnInterface: ParcelFileDescriptor? = null
    private var workerJob: Job? = null
    
    private lateinit var dbHelper: SecurityDatabaseHelper
    private val DB_PASSPHRASE: String by lazy { com.securitynav.security.data.security.KeyStoreManager(this).getMasterPassphrase() }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: LocalVpnService created")
        dbHelper = SecurityDatabaseHelper(this)
        com.securitynav.security.monitor.CriticalAppMonitor.startMonitoring(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: starting VPN service (startId=$startId)")
        
        try {
            startForegroundIfNeeded()
            val monitorOnly = intent?.getBooleanExtra("monitor_only", true) ?: true
            
            try {
                val builder = Builder()
                    .setSession("SecurityNavVPN")
                    .addAddress("10.0.0.2", 32)
                    .setMtu(1500)
                if (!monitorOnly) {
                    builder.addRoute("0.0.0.0", 0)
                }

                vpnInterface?.close()
                vpnInterface = builder.establish()

                if (vpnInterface != null) {
                    workerJob?.cancel()
                    workerJob = serviceScope.launch {
                        runVpnLoop(vpnInterface!!, monitorOnly)
                    }
                } else {
                    stopSelf()
                }
            } catch (t: Throwable) {
                stopSelf()
            }
        } catch (e: Exception) {
            stopSelf()
        }
        return Service.START_STICKY
    }

    private suspend fun runVpnLoop(pfd: ParcelFileDescriptor, monitorOnly: Boolean) {
        var input: FileInputStream? = null
        var output: FileOutputStream? = null
        
        var packetCount = 0
        
        try {
            input = FileInputStream(pfd.fileDescriptor)
            output = FileOutputStream(pfd.fileDescriptor)
            val buffer = ByteArray(32 * 1024)

            while (coroutineContext.isActive) {
                try {
                    val read = withContext(Dispatchers.IO) { 
                        input.read(buffer) 
                    }
                    
                    if (read > 0) {
                        // Check for loopback (127.x.x.x)
                        var isLoopback = false
                        if (read >= 20 && (buffer[0].toInt() shr 4) == 4) { // IPv4
                            val srcIp1 = buffer[12].toInt() and 0xFF
                            val destIp1 = buffer[16].toInt() and 0xFF
                            if (srcIp1 == 127 || destIp1 == 127) {
                                isLoopback = true
                            }
                        }
                        
                        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork = cm.activeNetwork
                        val caps = cm.getNetworkCapabilities(activeNetwork)
                        val isValidated = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
                        

                        if (!isLoopback && isValidated) {
                            NetworkMonitor.recordInbound(read.toLong())
                            
                            try {
                                if (read >= 20 && (buffer[0].toInt() shr 4) == 4) { // IPv4
                                    val ihl = buffer[0].toInt() and 0x0F
                                    val ipHeaderLength = ihl * 4
                                    val protocol = buffer[9].toInt() and 0xFF
                                    
                                    val srcIp = "${buffer[12].toInt() and 0xFF}.${buffer[13].toInt() and 0xFF}.${buffer[14].toInt() and 0xFF}.${buffer[15].toInt() and 0xFF}"
                                    val destIp = "${buffer[16].toInt() and 0xFF}.${buffer[17].toInt() and 0xFF}.${buffer[18].toInt() and 0xFF}.${buffer[19].toInt() and 0xFF}"
                                    
                                    var srcPort = 0
                                    var destPort = 0
                                    var protoStr = "OTHER"
                                    
                                    if (protocol == 6 && read >= ipHeaderLength + 4) { // TCP
                                        protoStr = "TCP"
                                        srcPort = ((buffer[ipHeaderLength].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 1].toInt() and 0xFF)
                                        destPort = ((buffer[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 3].toInt() and 0xFF)
                                    } else if (protocol == 17 && read >= ipHeaderLength + 4) { // UDP
                                        protoStr = "UDP"
                                        srcPort = ((buffer[ipHeaderLength].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 1].toInt() and 0xFF)
                                        destPort = ((buffer[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 3].toInt() and 0xFF)
                                    }
                                    
                                    val isOutbound = srcIp == "10.0.0.2"
                                    
                                    com.securitynav.security.engine.PacketAnalyzer.addPacket(
                                        com.securitynav.security.engine.RealTrafficLog(
                                            isOutbound = isOutbound,
                                            protocol = protoStr,
                                            port = if (isOutbound) destPort else srcPort,
                                            sourceIp = srcIp,
                                            destinationIp = destIp,
                                            payloadSize = read,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                // Ignore parse errors
                            }
                        }


                        withContext(Dispatchers.IO) { 
                            output.write(buffer, 0, read) 
                        }
                        
                        if (!isLoopback && isValidated) {
                            NetworkMonitor.recordOutbound(read.toLong())
                        }
                    } else {
                        delay(50)
                    }
                } catch (ioEx: IOException) {
                    if (coroutineContext.isActive) {
                        delay(100)
                    }
                }
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "runVpnLoop: exception", e)
        } finally {
            closeQuietly(input)
            closeQuietly(output)
        }
    }
    
    

    private fun closeQuietly(resource: Any?) {
        try {
            when (resource) {
                is FileInputStream -> resource.close()
                is FileOutputStream -> resource.close()
                is AutoCloseable -> resource.close()
            }
        } catch (ex: Exception) {}
    }

    private fun startForegroundIfNeeded() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        return try {
            val intent = Intent(this, Class.forName("com.securitynav.security.ui.MainActivity"))
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            NotificationCompat.Builder(this, "vpn_service_channel")
                .setContentTitle("SecurityNav VPN")
                .setContentText("Monitoring Network Packets...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
        } catch (e: Exception) {
            NotificationCompat.Builder(this, "vpn_service_channel")
                .setContentTitle("SecurityNav VPN")
                .setContentText("Monitoring...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build()
        }
    }

    override fun onDestroy() {
        try {
            workerJob?.cancel()
            serviceScope.cancel()
            vpnInterface?.close()
        } catch (e: Exception) {}
        finally {
            vpnInterface = null
            super.onDestroy()
        }
    }

    override fun onRevoke() {
        onDestroy()
        super.onRevoke()
    }
}
