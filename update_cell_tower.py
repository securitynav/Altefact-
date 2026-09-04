import re

with open("app/src/main/java/com/securitynav/security/ui/CellTowerFragment.kt", "r") as f:
    content = f.read()

imports_to_add = """
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.Gradient
import com.securitynav.security.utils.NotificationHelper
"""
content = content.replace("import com.google.maps.android.compose.rememberCameraPositionState\n", "import com.google.maps.android.compose.rememberCameraPositionState\n" + imports_to_add)

# State additions
state_additions = """
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
"""

content = content.replace("    var detectedTowers by remember { mutableStateOf(listOf<CellTower>()) }", 
                          "    var detectedTowers by remember { mutableStateOf(listOf<CellTower>()) }\n" + state_additions)


# Header Replace
header_original = """        Text(
            text = "TRIANGULACIÓN DE ANTENAS",
            color = Color(0xFF00F0FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )
        Text(
            text = "Geolocalización Forense & Detección IMSI",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )"""

header_new = """        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
        }"""
        
content = content.replace(header_original, header_new)

# List toggle replace
list_original = """        if (detectedTowers.isEmpty()) {
            Text("Escaneando frecuencias base...", color = Color.Gray, fontSize = 14.sp)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(detectedTowers) { tower ->
                    TowerCardWithCoordinates(tower = tower)
                }
            }
        }"""
        
list_new = """        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
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
    }"""

content = content.replace(list_original, list_new)

heatmap_composable = """
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
"""

content = content + "\n" + heatmap_composable

with open("app/src/main/java/com/securitynav/security/ui/CellTowerFragment.kt", "w") as f:
    f.write(content)

print("Patch applied.")
