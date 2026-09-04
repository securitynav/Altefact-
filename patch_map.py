import re

with open("app/src/main/java/com/securitynav/security/ui/CellTowerFragment.kt", "r") as f:
    content = f.read()

imports = """import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
"""
content = content.replace("import java.util.Locale", "import java.util.Locale\n" + imports)

map_code = """
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
"""

content = re.sub(
    r'// Mostrar coordenadas resultantes[\s\S]*?(?=            }\n        }\n    }\n})',
    map_code.strip() + '\n',
    content
)

with open("app/src/main/java/com/securitynav/security/ui/CellTowerFragment.kt", "w") as f:
    f.write(content)
