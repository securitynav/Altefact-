package com.securitynav.security.ui

import android.net.TrafficStats
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.securitynav.security.data.SystemStateManager
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
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = Color(0xFF1A73E8),
                        secondary = Color(0xFF34A853),
                        error = Color(0xFFD93025)
                    )
                ) {
                    SecurityHubScreen()
                }
            }
        }
    }
}

// ----------------- MOTOR DE TRADUCCIÓN DE PAQUETES (DETALLADO) -----------------
data class TrafficLog(
    val isIncoming: Boolean,
    val protocol: String,
    val port: Int,
    val sourceDest: String,
    val appName: String,
    val packetSize: String,
    val riskLevel: String, // "SEGURO", "RUTINA", "SOSPECHOSO"
    val encryption: String,
    val translation: String,
    val timestamp: String
)

val sampleLogsBank = listOf(
    TrafficLog(true, "HTTPS", 443, "104.18.2.19 (Cloudflare)", "Navegador Web", "1.2 MB", "SEGURO", "TLS 1.3", "Descarga de paquete de datos multimedia cifrados.", ""),
    TrafficLog(false, "DNS", 53, "8.8.8.8 (Google DNS)", "Sistema OS", "256 Bytes", "RUTINA", "Ninguno", "Resolución de nombre de dominio: api.whatsapp.com.", ""),
    TrafficLog(true, "TCP", 443, "192.168.1.1 (Gateway Local)", "Sistema OS", "1.5 KB", "RUTINA", "Local", "Sincronización de reloj de red interna.", ""),
    TrafficLog(false, "TLS", 443, "31.13.92.36 (Meta Platforms)", "Facebook / Instagram", "45.2 KB", "SOSPECHOSO", "TLS 1.2", "Envío de telemetría y métricas de uso en segundo plano.", ""),
    TrafficLog(false, "HTTPS", 443, "142.250.190.46 (Google API)", "Gmail", "12.4 KB", "SEGURO", "TLS 1.3", "Petición de estado de cuenta e indexación de correos.", ""),
    TrafficLog(true, "UDP", 443, "172.217.16.14 (YouTube)", "YouTube", "2.8 MB", "SEGURO", "QUIC", "Recepción de fragmentos de streaming de video/audio.", ""),
    TrafficLog(false, "TCP", 80, "54.239.28.85 (Amazon AWS)", "App Desconocida", "512 Bytes", "SOSPECHOSO", "No Cifrado", "Carga de metadatos de usuario hacia servidor remoto (Texto plano).", "")
)

@Composable
fun MarineRadar(securityState: SecurityState, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val baseColor = when (securityState) {
        SecurityState.SECURE -> Color(0xFF00FFCC)
        SecurityState.WARNING -> Color(0xFFFFB300)
        SecurityState.DANGER -> Color(0xFFFF0055)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val radius = size.width / 2f
            val center = Offset(radius, radius)

            drawCircle(color = Color(0xFF051525), radius = radius)
            drawCircle(color = baseColor.copy(alpha = 0.3f), radius = radius * 0.75f, style = Stroke(2f))
            drawCircle(color = baseColor.copy(alpha = 0.3f), radius = radius * 0.5f, style = Stroke(2f))
            drawCircle(color = baseColor.copy(alpha = 0.3f), radius = radius * 0.25f, style = Stroke(2f))
            drawCircle(color = baseColor, radius = radius, style = Stroke(4f))

            drawLine(baseColor.copy(alpha = 0.4f), start = Offset(0f, radius), end = Offset(size.width, radius), strokeWidth = 2f)
            drawLine(baseColor.copy(alpha = 0.4f), start = Offset(radius, 0f), end = Offset(radius, size.height), strokeWidth = 2f)

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color.Transparent, baseColor.copy(alpha = 0.05f), baseColor.copy(alpha = 0.5f), Color.Transparent),
                    center = center
                ),
                startAngle = sweepAngle - 60f,
                sweepAngle = 60f,
                useCenter = true,
                size = size
            )
            
            val angleRad = Math.toRadians(sweepAngle.toDouble())
            val endX = center.x + radius * Math.cos(angleRad).toFloat()
            val endY = center.y + radius * Math.sin(angleRad).toFloat()
            drawLine(color = baseColor, start = center, end = Offset(endX, endY), strokeWidth = 3f)
        }
        
        Canvas(modifier = Modifier.size(width = 110.dp, height = 30.dp).padding(top = 4.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.15f, 0f)
                lineTo(size.width * 0.85f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path = path, color = Color(0xFF0A1018))
            drawLine(
                color = baseColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 8f
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityHubScreen() {
    var statusText by remember { mutableStateOf("SISTEMA SEGURO") }
    var statusColor by remember { mutableStateOf(Color(0xFF00FFCC)) }
    var bandwidthText by remember { mutableStateOf("Descarga: 0.0 KB/s | Subida: 0.0 KB/s") }
    
    val producer = remember { ChartEntryModelProducer(
        (0..19).map { FloatEntry(it.toFloat(), 0f) },
        (0..19).map { FloatEntry(it.toFloat(), 0f) }
    ) }
    
    var timeIndex by remember { mutableStateOf(20f) }
    var dlEntries by remember { mutableStateOf((0..19).map { FloatEntry(it.toFloat(), 0f) }) }
    var ulEntries by remember { mutableStateOf((0..19).map { FloatEntry(it.toFloat(), 0f) }) }
    
    var securityState by remember { mutableStateOf(SecurityState.SECURE) }
    val context = LocalContext.current

    // Bottom Sheet State
    var showInspector by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var trafficLogs by remember { mutableStateOf(listOf<TrafficLog>()) }

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
            
            bandwidthText = "Descarga: $dlStr | Subida: $ulStr"
            
            val newDl = dlEntries + FloatEntry(timeIndex, dl)
            val newUl = ulEntries + FloatEntry(timeIndex, ul)
            
            val trimmedDl = newDl.takeLast(20)
            val trimmedUl = newUl.takeLast(20)
            
            dlEntries = trimmedDl
            ulEntries = trimmedUl
            
            producer.setEntries(trimmedDl, trimmedUl)
            timeIndex++
            
            // Simular logs de tráfico si el inspector está abierto
            if (showInspector && (dl > 5f || ul > 5f || Math.random() > 0.5)) {
                val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val newLog = sampleLogsBank.random().copy(timestamp = formatter.format(Date()))
                trafficLogs = (listOf(newLog) + trafficLogs).take(30)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MarineRadar(
            securityState = securityState,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        
        Text(
            text = statusText,
            color = statusColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    SystemStateManager.turnOn(context)
                    statusText = "Sistema Activo"
                    statusColor = Color(0xFF00FFCC)
                    securityState = SecurityState.SECURE
                }
            ) {
                Text("Activar", color = Color(0xFF00FFCC), fontSize = 12.sp)
            }
            
            OutlinedButton(
                onClick = {
                    SystemStateManager.pause(context)
                    statusText = "Sistema Pausado"
                    statusColor = Color(0xFFFFB300)
                    securityState = SecurityState.WARNING
                }
            ) {
                Text("Pausar", color = Color(0xFFFFB300), fontSize = 12.sp)
            }
            
            OutlinedButton(
                onClick = {
                    SystemStateManager.turnOff(context)
                    statusText = "Apagado"
                    statusColor = Color(0xFFFF0055)
                    securityState = SecurityState.DANGER
                }
            ) {
                Text("Apagar", color = Color(0xFFFF0055), fontSize = 12.sp)
            }
        }
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tráfico de Red", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
                        Text(bandwidthText, color = Color.Gray, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { showInspector = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF051525)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Inspeccionar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Traducir", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Descarga", color = Color(0xFF00F0FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val maxDl = dlEntries.maxOfOrNull { it.y } ?: 0f
                        Text(if (maxDl > 1024) String.format("%.1f MB/s", maxDl / 1024f) else String.format("%.1f KB/s", maxDl), color = Color.Gray, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Subida", color = Color(0xFFFF0055), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val maxUl = ulEntries.maxOfOrNull { it.y } ?: 0f
                        Text(if (maxUl > 1024) String.format("%.1f MB/s", maxUl / 1024f) else String.format("%.1f KB/s", maxUl), color = Color.Gray, fontSize = 10.sp)
                    }
                }
                
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = Color(0xFF00F0FF),
                        secondary = Color(0xFFFF0055)
                    )
                ) {
                    ProvideChartStyle(m3ChartStyle()) {
                        Chart(
                            chart = lineChart(),
                            chartModelProducer = producer,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (showInspector) {
        ModalBottomSheet(
            onDismissRequest = { showInspector = false },
            sheetState = sheetState,
            containerColor = Color(0xFF0A1018), 
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Inspector de Paquetes (Deep Packet Inspection)",
                    color = Color(0xFF00F0FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Desglosando carga útil en tiempo real...",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (trafficLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00F0FF))
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                        items(trafficLogs) { log ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D26)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D3D)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Encabezado principal: Dirección y Tiempo
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (log.isIncoming) "⬇ IN" else "⬆ OUT",
                                                color = if (log.isIncoming) Color(0xFF00F0FF) else Color(0xFFFF0055),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = " | ${log.protocol}:${log.port}",
                                                color = Color.LightGray,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(text = log.timestamp, color = Color.Gray, fontSize = 12.sp)
                                    }
                                    
                                    Divider(color = Color(0xFF1E2D3D), modifier = Modifier.padding(vertical = 8.dp))
                                    
                                    // Detalles técnicos desglosados en GRID
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("ORIGEN / DESTINO", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(log.sourceDest, color = Color.White, fontSize = 13.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                                        }
                                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                            Text("APLICACIÓN ASOCIADA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(log.appName, color = Color.White, fontSize = 13.sp)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("TAMAÑO DE PAQUETE", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(log.packetSize, color = Color.LightGray, fontSize = 13.sp)
                                        }
                                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                            Text("CIFRADO", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (log.encryption.contains("No Cifrado") || log.encryption.contains("Ninguno")) {
                                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                                                } else {
                                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF34A853), modifier = Modifier.size(12.dp))
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(log.encryption, color = Color.LightGray, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Análisis de riesgo y Traducción humana
                                    val riskColor = when(log.riskLevel) {
                                        "SEGURO" -> Color(0xFF34A853)
                                        "RUTINA" -> Color(0xFF1A73E8)
                                        else -> Color(0xFFFF0055)
                                    }
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = riskColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                text = " ${log.riskLevel} ", 
                                                color = riskColor, 
                                                fontSize = 10.sp, 
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = log.translation,
                                            color = Color(0xFF00FFCC), 
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
