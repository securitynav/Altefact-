package com.securitynav.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import java.util.Locale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.Gradient
import com.securitynav.security.utils.NotificationHelper

import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer


class CellTowerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme(
                        background = Color(0xFF0A131F),
                        surface = Color(0xFF0A131F)
                    )
                ) {
                    CellTowerDetectorScreen()
                }
            }
        }
    }
}

data class CellTower(
    val id: String,
    val type: String, // "4G/LTE", "5G", "GSM (2G)"
    val signalStrength: Int,
    val isFake: Boolean,
    val distance: Float,
    val pci: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val calculationProgress: Float = 0f,
    val status: String = "ESCANEO RF..."
)

@Composable
fun CellTowerDetectorScreen() {
    var isScanning by remember { mutableStateOf(true) }
    var detectedTowers by remember { mutableStateOf(listOf<CellTower>()) }

    var rssiThreshold by remember { mutableStateOf(-65) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf("LIST") }
    var rogueTowerExposureSeconds by remember { mutableStateOf(0) }
    val context = LocalContext.current
    
    LaunchedEffect(detectedTowers, rssiThreshold) {
        var exposed = false
        while(isActive) {
            delay(1000)
            val hasRogueOrStrong = detectedTowers.any { it.isFake || it.signalStrength > rssiThreshold }
            if (hasRogueOrStrong) {
                rogueTowerExposureSeconds++
                if (rogueTowerExposureSeconds == 5) { // simulate > 5 minutes
                    NotificationHelper.showRogueTowerAlert(context)
                }
            } else {
                rogueTowerExposureSeconds = 0
            }
        }
    }

    
    LaunchedEffect(isScanning) {
        if (isScanning) {
            detectedTowers = emptyList()
            delay(1000)
            
            var currentTowers = listOf(
                CellTower("Cell-A92B", "4G/LTE", -75, false, 0.4f, 120),
                CellTower("Cell-B104", "5G", -80, false, 0.8f, 135)
            )
            detectedTowers = currentTowers

            // SIMULACIÓN DE PROCESO COMPLEJO DE TRIANGULACIÓN (TDOA + RSSI)
            for (i in 1..50) {
                if (!isScanning) break
                delay(60)
                currentTowers = currentTowers.map { tower ->
                    val newProgress = (tower.calculationProgress + 0.02f).coerceAtMost(1f)
                    val newStatus = when {
                        newProgress >= 1f -> "COORDENADAS FIJADAS"
                        newProgress > 0.6f -> "APLICANDO ALGORITMO TDOA..."
                        newProgress > 0.3f -> "MIDIENDO TIEMPO DE VUELO..."
                        else -> "TRIANGULANDO POR RSSI..."
                    }
                    val newLat = if (newProgress >= 1f && tower.latitude == null) 19.4326 + (Math.random() * 0.02 - 0.01) else tower.latitude
                    val newLng = if (newProgress >= 1f && tower.longitude == null) -99.1332 + (Math.random() * 0.02 - 0.01) else tower.longitude
                    
                    tower.copy(calculationProgress = newProgress, status = newStatus, latitude = newLat, longitude = newLng)
                }
                detectedTowers = currentTowers
            }

            delay(1500)
            if (isScanning) {
                val fakeTower = CellTower("UNKNOWN-IMSI-CATCHER", "GSM (2G)", -55, true, 0.15f, 999, status = "ANOMALÍA DETECTADA...")
                currentTowers = currentTowers + fakeTower
                detectedTowers = currentTowers

                // PROCESO DE REVELACIÓN PARA ANTENA FALSA OFUSCADA
                for (i in 1..80) {
                    if (!isScanning) break
                    delay(50)
                    currentTowers = currentTowers.map { tower ->
                        if (tower.isFake) {
                            val newProgress = (tower.calculationProgress + 0.0125f).coerceAtMost(1f)
                            val newStatus = when {
                                newProgress >= 1f -> "UBICACIÓN HOSTIL EXPUESTA"
                                newProgress > 0.7f -> "BYPASS DE OFUSCACIÓN MAC..."
                                newProgress > 0.4f -> "FORZANDO REVELACIÓN GEOMÉTRICA..."
                                else -> "DETECTANDO CAMUFLAJE GPS..."
                            }
                            val newLat = if (newProgress >= 1f && tower.latitude == null) 19.4326 + (Math.random() * 0.002 - 0.001) else tower.latitude
                            val newLng = if (newProgress >= 1f && tower.longitude == null) -99.1332 + (Math.random() * 0.002 - 0.001) else tower.longitude
                            
                            tower.copy(calculationProgress = newProgress, status = newStatus, latitude = newLat, longitude = newLng)
                        } else tower
                    }
                    detectedTowers = currentTowers
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A131F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = "TRIANGULACIÓN DE ANTENAS",
                    color = Color(0xFF00F0FF),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
                )
                Text(
                    text = "Geolocalización Forense & Detección IMSI",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = { showSettingsDialog = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Configurar", tint = Color(0xFF00F0FF))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            TriangulationRadar(isScanning = isScanning, towers = detectedTowers)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { isScanning = !isScanning },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) Color(0xFF1E2D3D) else Color(0xFF00F0FF),
                contentColor = if (isScanning) Color(0xFF00F0FF) else Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(if (isScanning) "DETENER ESCANEO" else "INICIAR ESCANEO SECTORIAL")
        }

        Spacer(modifier = Modifier.height(16.dp))

        SignalFluctuationChart()
        
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Button(
                onClick = { viewMode = "LIST" },
                colors = ButtonDefaults.buttonColors(containerColor = if(viewMode == "LIST") Color(0xFF00F0FF) else Color(0xFF1E2D3D), contentColor = if(viewMode == "LIST") Color.Black else Color.White),
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 0.dp, bottomEnd = 0.dp)
            ) { Text("LISTA", fontSize = 12.sp) }
            Button(
                onClick = { viewMode = "HEATMAP" },
                colors = ButtonDefaults.buttonColors(containerColor = if(viewMode == "HEATMAP") Color(0xFF00F0FF) else Color(0xFF1E2D3D), contentColor = if(viewMode == "HEATMAP") Color.Black else Color.White),
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp)
            ) { Text("MAPA DE CALOR", fontSize = 12.sp) }
        }

        if (detectedTowers.isEmpty()) {
            Text("Escaneando frecuencias base...", color = Color.Gray, fontSize = 14.sp)
        } else {
            if (viewMode == "LIST") {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(detectedTowers) { tower ->
                        TowerCardWithCoordinates(tower = tower)
                    }
                }
            } else {
                HeatmapView(towers = detectedTowers)
            }
        }
        
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = Color(0xFF0A131F),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = { Text("Configuración de Umbrales") },
            text = {
                Column {
                    Text("Umbral de señal sospechosa (RSSI): $rssiThreshold dBm", fontSize = 14.sp)
                    Slider(
                        value = rssiThreshold.toFloat(),
                        onValueChange = { rssiThreshold = it.toInt() },
                        valueRange = -110f..-40f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00F0FF),
                            activeTrackColor = Color(0xFF00F0FF),
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                    Text("Si se detecta una señal más fuerte que este umbral de manera sostenida, se emitirá una alerta.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("GUARDAR", color = Color(0xFF00F0FF))
                }
            }
        )
    }
    }
}

@Composable
fun TriangulationRadar(isScanning: Boolean, towers: List<CellTower>) {
    val transition = rememberInfiniteTransition()
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.size(260.dp)) {
        val radius = size.width / 2f
        val center = Offset(radius, radius)
        
        drawCircle(color = Color(0xFF0A131F), radius = radius)
        drawCircle(color = Color(0xFF00F0FF).copy(alpha = 0.3f), radius = radius, style = Stroke(2f))
        drawCircle(color = Color(0xFF00F0FF).copy(alpha = 0.2f), radius = radius * 0.66f, style = Stroke(1f))
        drawCircle(color = Color(0xFF00F0FF).copy(alpha = 0.1f), radius = radius * 0.33f, style = Stroke(1f))
        
        drawLine(Color(0xFF00F0FF).copy(alpha = 0.4f), Offset(0f, radius), Offset(size.width, radius), 1f)
        drawLine(Color(0xFF00F0FF).copy(alpha = 0.4f), Offset(radius, 0f), Offset(radius, size.height), 1f)

        if (isScanning) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color.Transparent, Color(0xFF00F0FF).copy(alpha = 0.1f), Color(0xFF00F0FF).copy(alpha = 0.6f), Color.Transparent),
                    center = center
                ),
                startAngle = sweepAngle - 90f,
                sweepAngle = 90f,
                useCenter = true,
                size = size
            )
        }

        towers.forEachIndexed { index, tower ->
            val angle = if (tower.isFake) 45.0 else (120.0 * index + 30.0)
            val distanceFactor = tower.distance / 1.0f 
            val towerRadius = radius * distanceFactor
            
            val angleRad = Math.toRadians(angle)
            val x = center.x + towerRadius * cos(angleRad).toFloat()
            val y = center.y + towerRadius * sin(angleRad).toFloat()
            
            val towerColor = if (tower.isFake) Color(0xFFFF0055) else Color(0xFF00FFCC)
            
            drawCircle(
                color = towerColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            
            if (tower.isFake) {
                drawCircle(
                    color = towerColor.copy(alpha = 0.3f),
                    radius = 16.dp.toPx() + (sweepAngle % 10),
                    center = Offset(x, y)
                )
            }
            
            // Draw connecting line to center if coordinates are locked
            if (tower.calculationProgress >= 1f) {
                drawLine(
                    color = towerColor.copy(alpha = 0.5f),
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Composable
fun TowerCardWithCoordinates(tower: CellTower) {
    val cardColor = if (tower.isFake) Color(0xFF2A0A10) else Color(0xFF0A131F)
    val borderColor = if (tower.isFake) Color(0xFFFF0055) else Color(0xFF1E2D3D)
    val iconTint = if (tower.isFake) Color(0xFFFF0055) else Color(0xFF00FFCC)
    
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(borderColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (tower.isFake) Icons.Default.Warning else Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (tower.isFake) "¡ANTENA FALSA (IMSI CATCHER)!" else "Celda Segura: ${tower.id}",
                        color = if (tower.isFake) Color(0xFFFF0055) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("PCI: ${tower.pci}", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text(" • ", color = Color.Gray, fontSize = 11.sp)
                        Text(tower.type, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(" • ", color = Color.Gray, fontSize = 11.sp)
                        Text("Señal: ${tower.signalStrength} dBm", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Proceso Complejo de Coordenadas
            Text(
                text = tower.status,
                color = if (tower.calculationProgress >= 1f) iconTint else Color(0xFFFFB300),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Barra de progreso de cálculo
            if (tower.calculationProgress < 1f) {
                LinearProgressIndicator(
                    progress = tower.calculationProgress,
                    color = iconTint,
                    trackColor = Color(0xFF1E2D3D),
                    modifier = Modifier.fillMaxWidth().height(4.dp)
                )
            } else {
                // Mostrar coordenadas resultantes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    val latStr = String.format(Locale.US, "%.6f", tower.latitude)
                    val lngStr = String.format(Locale.US, "%.6f", tower.longitude)
                    Text(
                        text = "LAT: $latStr | LNG: $lngStr",
                        color = iconTint,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (tower.latitude != null && tower.longitude != null) {
                    val position = LatLng(tower.latitude, tower.longitude)
                    val cameraPositionState = rememberCameraPositionState {
                        position.let {
                            this.position = CameraPosition.fromLatLngZoom(position, 15f)
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        GoogleMap(
                            modifier = Modifier.matchParentSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = MarkerState(position = position),
                                title = if (tower.isFake) "IMSI Catcher" else "Celda Segura"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SignalFluctuationChart() {
    val modelProducer = remember { ChartEntryModelProducer() }
    
    LaunchedEffect(Unit) {
        val entries = mutableListOf<FloatEntry>()
        // Initialize with normal fluctuation for the past hour (simulated)
        for (i in 0..50) {
            val isRogueSpike = i > 40 && i < 45
            val rssi = if (isRogueSpike) {
                -50f + (Math.random() * 5).toFloat() // Rogue tower spike
            } else {
                -85f + (Math.random() * 10).toFloat() // Normal noise
            }
            entries.add(FloatEntry(x = i.toFloat(), y = rssi))
        }
        modelProducer.setEntries(entries)
        
        // Real-time update
        var xIndex = 51f
        while(isActive) {
            delay(1000)
            val isRogue = Math.random() > 0.8
            val rssi = if (isRogue) -50f + (Math.random() * 5).toFloat() else -85f + (Math.random() * 10).toFloat()
            entries.add(FloatEntry(x = xIndex, y = rssi))
            if (entries.size > 60) {
                entries.removeAt(0)
            }
            modelProducer.setEntries(entries.toList())
            xIndex++
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A131F)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D3D)),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ANÁLISIS DE FLUCTUACIÓN (RSSI) - ÚLTIMA HORA",
                color = Color(0xFF00F0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Chart(
                chart = lineChart(),
                chartModelProducer = modelProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        }
    }
}


@Composable
fun HeatmapView(towers: List<CellTower>) {
    val context = LocalContext.current
    
    val locations = towers.filter { it.latitude != null && it.longitude != null }
        .map { LatLng(it.latitude!!, it.longitude!!) }
        
    val cameraPositionState = rememberCameraPositionState {
        val firstLoc = locations.firstOrNull() ?: LatLng(19.4326, -99.1332)
        position = CameraPosition.fromLatLngZoom(firstLoc, 13f)
    }

    Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        ) {
            if (locations.isNotEmpty()) {
                val provider = remember(locations) {
                    val gradientColors = intArrayOf(
                        android.graphics.Color.rgb(0, 255, 0),    // green
                        android.graphics.Color.rgb(255, 255, 0),  // yellow
                        android.graphics.Color.rgb(255, 0, 0)     // red
                    )
                    val gradientStartPoints = floatArrayOf(0.2f, 0.6f, 1f)
                    val gradient = Gradient(gradientColors, gradientStartPoints)
                    
                    HeatmapTileProvider.Builder()
                        .data(locations)
                        .gradient(gradient)
                        .radius(40)
                        .build()
                }
                TileOverlay(tileProvider = provider)
                
                locations.forEach { loc ->
                    Marker(
                        state = MarkerState(position = loc),
                        icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED)
                    )
                }
            }
        }
    }
}
