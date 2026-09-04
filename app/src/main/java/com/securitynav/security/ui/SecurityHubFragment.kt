package com.securitynav.security.ui

import android.content.Intent
import android.net.TrafficStats
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.securitynav.security.billing.PaywallBottomSheet
import com.securitynav.security.billing.SubscriptionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecurityHubFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SecurityHubScreen()
                }
            }
        }
    }
}

data class TrafficLog(
    val isOutbound: Boolean,
    val protocol: String,
    val port: Int,
    val destination: String,
    val appName: String,
    val payloadSize: String,
    val status: String,
    val encryption: String,
    val insights: String,
    val timestamp: String
)

@Composable
fun SecurityHubScreen() {
    val context = LocalContext.current
    val isPro by SubscriptionManager.isProUser.collectAsState(initial = false)
    var showPaywall by remember { mutableStateOf(false) }

    var isMasterArmed by remember { mutableStateOf(false) }
    var heuristicScanner by remember { mutableStateOf(false) }
    var towerGuard by remember { mutableStateOf(false) }
    var accessibilityShield by remember { mutableStateOf(false) }
    var overlayGuard by remember { mutableStateOf(false) }
    var netGuardDpi by remember { mutableStateOf(false) }
    var appProcessDpi by remember { mutableStateOf(false) }
    var hardwareVault by remember { mutableStateOf(false) }

    var bandwidthText by remember { mutableStateOf("Calculando...") }
    
    // Simulate Traffic Logs
    var trafficLogs by remember { mutableStateOf(listOf<TrafficLog>()) }

    LaunchedEffect(Unit) {
        var lastRx = TrafficStats.getTotalRxBytes()
        var lastTx = TrafficStats.getTotalTxBytes()
        while (isActive) {
            val currentRx = TrafficStats.getTotalRxBytes()
            val currentTx = TrafficStats.getTotalTxBytes()
            val rxSpeed = (currentRx - lastRx) / 1024
            val txSpeed = (currentTx - lastTx) / 1024
            bandwidthText = "DL: ${rxSpeed} KB/s | UL: ${txSpeed} KB/s"
            lastRx = currentRx
            lastTx = currentTx
            
            if (isMasterArmed && Math.random() > 0.6) {
                val newLog = generateRandomTrafficLog()
                trafficLogs = listOf(newLog) + trafficLogs.take(15)
            }
            
            delay(1000)
        }
    }

    if (showPaywall) {
        PaywallBottomSheet(
            onDismiss = { showPaywall = false },
            subscriptionManager = SubscriptionManager
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Marine Radar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            MarineRadar(isScanning = isMasterArmed)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Master Lock
        val lockColor = if (isMasterArmed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        val lockText = if (isMasterArmed) "SISTEMA ARMADO" else "SISTEMA DESARMADO"

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
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
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isMasterArmed) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Master Lock",
                    tint = lockColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(lockText, color = lockColor, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 18.sp)
                    Text("Toque para activar/desactivar el escudo", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("TRÁFICO EN TIEMPO REAL - $bandwidthText", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        // Traffic Logs List
        LazyColumn(modifier = Modifier.weight(1f)) {
            if (trafficLogs.isEmpty()) {
                item {
                    Text(
                        "Esperando paquetes de red...", 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f), 
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(trafficLogs) { log ->
                TrafficLogItem(log = log)
            }
        }
    }
}

fun generateRandomTrafficLog(): TrafficLog {
    val isOut = Math.random() > 0.5
    val apps = listOf("WhatsApp", "Chrome", "Instagram", "App Desconocida", "Google Services")
    val dests = listOf("104.16.24.34 (Cloudflare)", "54.239.28.85 (Amazon AWS)", "172.217.16.14 (Google)", "185.60.216.35 (Meta)")
    val ports = listOf(443, 80, 53, 8080)
    
    val app = apps.random()
    val isUnsafe = app == "App Desconocida"
    
    return TrafficLog(
        isOutbound = isOut,
        protocol = if (ports.random() == 443) "TCP" else "UDP",
        port = ports.random(),
        destination = dests.random(),
        appName = app,
        payloadSize = "${(10..1024).random()} KB",
        status = if (isUnsafe) "SOSPECHOSO" else "SEGURO",
        encryption = if (isUnsafe) "No Cifrado" else "TLS 1.3",
        insights = if (isUnsafe) "Envío de metadatos a servidor desconocido en texto plano." else "Tráfico encriptado normal.",
        timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    )
}

@Composable
fun TrafficLogItem(log: TrafficLog) {
    var expanded by remember { mutableStateOf(false) }
    val color = if (log.status == "SEGURO") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (log.isOutbound) "⬆ SALIENTE" else "⬇ ENTRANTE", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(log.timestamp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f), fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(log.appName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${log.destination} : ${log.port}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Protocolo: ${log.protocol}", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), fontSize = 12.sp)
                        Text("Cifrado: ${log.encryption}", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tamaño: ${log.payloadSize}", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), fontSize = 12.sp)
                        Text("Estado: ${log.status}", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Análisis DPI:", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(log.insights, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MarineRadar(isScanning: Boolean) {
    val transition = rememberInfiniteTransition()
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Draw Background circles
        for (i in 1..3) {
            drawCircle(
                color = Color(0xFF00F0FF).copy(alpha = 0.3f),
                radius = radius * (i / 3f),
                center = center,
                style = Stroke(width = 1f)
            )
        }
        
        // Draw Crosshairs
        drawLine(
            color = Color(0xFF00F0FF).copy(alpha = 0.3f),
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 1f
        )
        drawLine(
            color = Color(0xFF00F0FF).copy(alpha = 0.3f),
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 1f
        )

        // Draw sweeping arc if scanning
        if (isScanning) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color.Transparent, Color(0xFF00F0FF).copy(alpha = 0.5f)),
                    center = center
                ),
                startAngle = sweepAngle - 90f,
                sweepAngle = 90f,
                useCenter = true,
                size = size
            )
            
            // Draw simulated blips
            if ((sweepAngle % 180) < 30) {
                 drawCircle(
                     color = Color.Red,
                     radius = 4.dp.toPx(),
                     center = Offset(center.x + radius * 0.6f, center.y - radius * 0.4f)
                 )
            }
            if ((sweepAngle % 360) in 200.0..230.0) {
                 drawCircle(
                     color = Color.Green,
                     radius = 3.dp.toPx(),
                     center = Offset(center.x - radius * 0.5f, center.y + radius * 0.5f)
                 )
            }
        }
    }
}
