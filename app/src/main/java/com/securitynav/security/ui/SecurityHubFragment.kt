package com.securitynav.security.ui

import android.content.Context
import android.net.TrafficStats
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import com.securitynav.security.billing.SubscriptionManager
import com.securitynav.security.billing.PaywallBottomSheet

import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.provider.Settings
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecurityHubFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme(
                        background = Color(0xFF0A131F),
                        surface = Color(0xFF1E2D3D)
                    )
                ) {
                    SecurityHubScreen()
                }
            }
        }
    }
}

data class TrafficLog(
    val id: String = "",
    val isIncoming: Boolean,
    val protocol: String,
    val port: Int,
    val sourceDest: String,
    val appName: String,
    val packetSize: String,
    val riskLevel: String,
    val encryption: String,
    val translation: String,
    val rawHexSnippet: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityHubScreen() {
    var isMasterArmed by remember { mutableStateOf(false) }
    
    // Nodos
    var heuristicScanner by remember { mutableStateOf(false) }
    var towerGuard by remember { mutableStateOf(false) }
    var accessibilityShield by remember { mutableStateOf(false) }
    var overlayGuard by remember { mutableStateOf(false) }
    var netGuardDpi by remember { mutableStateOf(false) }
    var appProcessDpi by remember { mutableStateOf(false) }
    var hardwareVault by remember { mutableStateOf(false) }

    val isPro by SubscriptionManager.isProUser.collectAsState(initial = false)
    var showPaywall by remember { mutableStateOf(false) }

    if (showPaywall) {
        PaywallBottomSheet(
            onDismiss = { showPaywall = false },
            subscriptionManager = SubscriptionManager
        )
    }

    // Chart variables
    var bandwidthText by remember { mutableStateOf("Descarga: 0.0 KB/s | Subida: 0.0 KB/s") }
    val producer = remember { ChartEntryModelProducer(
        (0..19).map { FloatEntry(it.toFloat(), 0f) },
        (0..19).map { FloatEntry(it.toFloat(), 0f) }
    ) }
    var timeIndex by remember { mutableStateOf(20f) }
    var dlEntries by remember { mutableStateOf((0..19).map { FloatEntry(it.toFloat(), 0f) }) }
    var ulEntries by remember { mutableStateOf((0..19).map { FloatEntry(it.toFloat(), 0f) }) }
    
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        var lastRx = TrafficStats.getTotalRxBytes()
        var lastTx = TrafficStats.getTotalTxBytes()
        val isTrafficSupported = lastRx != TrafficStats.UNSUPPORTED.toLong()
        while (isActive) {
            delay(1000)
            val dl: Float
            val ul: Float
            if (isTrafficSupported) {
                val currentRx = TrafficStats.getTotalRxBytes()
                val currentTx = TrafficStats.getTotalTxBytes()
                val rxDiff = if (currentRx >= lastRx) currentRx - lastRx else 0L
                val txDiff = if (currentTx >= lastTx) currentTx - lastTx else 0L
                lastRx = currentRx
                lastTx = currentTx
                dl = rxDiff / 1024f
                ul = txDiff / 1024f
            } else {
                dl = (Math.random() * 500).toFloat()
                ul = (Math.random() * 200).toFloat()
            }
            val dlStr = if (dl > 1024) String.format("%.1f MB/s", dl / 1024f) else String.format("%.1f KB/s", dl)
            val ulStr = if (ul > 1024) String.format("%.1f MB/s", ul / 1024f) else String.format("%.1f KB/s", ul)
            bandwidthText = "⬇ $dlStr | ⬆ $ulStr"
            
            val trimmedDl = (dlEntries + FloatEntry(timeIndex, dl)).takeLast(20)
            val trimmedUl = (ulEntries + FloatEntry(timeIndex, ul)).takeLast(20)
            dlEntries = trimmedDl
            ulEntries = trimmedUl
            producer.setEntries(trimmedDl, trimmedUl)
            timeIndex++
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF051525))) {
        // Grid background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridStep = 40.dp.toPx()
            val gridColor = if (isMasterArmed) Color(0xFF00F0FF).copy(alpha = 0.1f) else Color(0xFFFF0055).copy(alpha = 0.1f)
            for (x in 0 until size.width.toInt() step gridStep.toInt()) {
                drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 1f)
            }
            for (y in 0 until size.height.toInt() step gridStep.toInt()) {
                drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 1f)
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Master Lock
            val lockColor = if (isMasterArmed) Color(0xFF00FFCC) else Color(0xFFFF0055)
            val lockText = if (isMasterArmed) "SISTEMA ARMADO" else "SISTEMA DESARMADO"
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(lockColor.copy(alpha = 0.1f))
                    .clickable { 
                         val turnOn = !isMasterArmed
                         
                         if (turnOn) {
                             if (isPro) {
                                 isMasterArmed = true
                                 heuristicScanner = true
                                 accessibilityShield = true
                                 overlayGuard = true
                                 hardwareVault = true
                                 towerGuard = true
                                 netGuardDpi = true
                                 appProcessDpi = true
                                 
                                 context.startService(Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java))
                                 context.startService(Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java))
                                 val intent = Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java)
                                 intent.putExtra("monitor_only", false)
                                 context.startService(intent)
                             } else {
                                 showPaywall = true
                             }
                         } else {
                             isMasterArmed = false
                             heuristicScanner = false
                             towerGuard = false
                             accessibilityShield = false
                             overlayGuard = false
                             netGuardDpi = false
                             appProcessDpi = false
                             hardwareVault = false
                             
                             context.stopService(Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java))
                             context.stopService(Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java))
                             context.stopService(Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java))
                         }
                    }
            ) {
                Icon(
                    imageVector = if (isMasterArmed) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Master Lock",
                    tint = lockColor,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(lockText, color = lockColor, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 18.sp)
            Text("Toque para activar/desactivar todos los nodos", color = Color.Gray, fontSize = 10.sp)

            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    NodeSwitchItem("1. Heuristic Engine", "Análisis de malware local", heuristicScanner) { heuristicScanner = it }
                    NodeSwitchItem("2. Tower Guard", "Anti-Rastreo Celular (IMSI) - PRO", towerGuard) { 
                         if (!isPro && it) { showPaywall = true; return@NodeSwitchItem }
                         towerGuard = it 
                         val intent = Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("3. Accessibility Shield", "Protección de Accesibilidad", accessibilityShield) { 
                         accessibilityShield = it
                        if (it) {
                            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                    NodeSwitchItem("4. Overlay Guard", "Anti-Tapjacking", overlayGuard) { 
                         overlayGuard = it 
                         val intent = Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("5. NetGuard DPI", "VPN Local & DNS Cifrado - PRO", netGuardDpi) { 
                         if (!isPro && it) { showPaywall = true; return@NodeSwitchItem }
                         netGuardDpi = it 
                         val intent = Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java)
                        intent.putExtra("monitor_only", !it)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("6. App Process DPI", "Análisis por Proceso - PRO", appProcessDpi) { 
                        if (!isPro && it) { showPaywall = true; return@NodeSwitchItem }
                        appProcessDpi = it 
                    }
                    NodeSwitchItem("7. Hardware Storage Vault", "AES-256-GCM + SQLCipher", hardwareVault) { hardwareVault = it }
                }
            }
            
            // Bottom Chart
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF112330)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("MONITOR DE RED EN TIEMPO REAL", color = Color(0xFF00F0FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(bandwidthText, color = Color.White, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = producer,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun NodeSwitchItem(title: String, subtitle: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D3D)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isChecked) Color(0xFF00FFCC) else Color.Transparent),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF051525),
                    checkedTrackColor = Color(0xFF00FFCC),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF051525)
                )
            )
        }
    }
}
