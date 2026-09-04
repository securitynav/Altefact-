package com.securitynav.security.monitor

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.securitynav.security.engine.DataLeakDetector
import com.securitynav.security.engine.LeakVector
import com.securitynav.security.engine.VaultManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random

object CriticalAppMonitor {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var isMonitoring = false
    private lateinit var appContext: Context
    private var lastLeakId: String? = null

    fun startMonitoring(context: Context) {
        if (isMonitoring) return
        isMonitoring = true
        appContext = context.applicationContext

        DataLeakDetector.leaks.onEach { leaks ->
            val latestLeak = leaks.firstOrNull() ?: return@onEach
            if (latestLeak.id == lastLeakId) return@onEach
            lastLeakId = latestLeak.id
            
            if (latestLeak.vector == LeakVector.APP) {
                val criticalApps = VaultManager.apps.value.filter { it.isCritical }
                for (app in criticalApps) {
                    if (latestLeak.source.contains(app.name, ignoreCase = true) || 
                        latestLeak.source.contains(app.packageName, ignoreCase = true)) {
                        
                        VaultManager.terminateApp(app.packageName)
                        showTerminationNotification(app.name)
                    }
                }
            }
        }.launchIn(scope)
    }

    private fun showTerminationNotification(appName: String) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(appContext, Class.forName("com.securitynav.security.ui.MainActivity"))
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notif = NotificationCompat.Builder(appContext, "vpn_service_channel")
            .setContentTitle("Aplicación Crítica Terminada")
            .setContentText("El sistema cerró $appName por transmitir datos sin cifrar.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
            
        notificationManager.notify(Random.nextInt(1000, 2000), notif)
    }
}
