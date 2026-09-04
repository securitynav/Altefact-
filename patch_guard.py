with open('app/src/main/java/com/securitynav/security/ui/GuardFragment.kt', 'w') as f:
    f.write("""package com.securitynav.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.securitynav.security.engine.AntivirusEngine
import com.securitynav.security.engine.ThreatAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuardFragment : Fragment() {

    private lateinit var antivirus: AntivirusEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        antivirus = AntivirusEngine(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    AppScannerScreen()
                }
            }
        }
    }

    @Composable
    fun AppScannerScreen() {
        val coroutineScope = rememberCoroutineScope()
        var results by remember { mutableStateOf<List<ThreatAnalysisResult>?>(null) }
        var isScanning by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A131F))
                .padding(16.dp)
        ) {
            Text(
                text = "Escáner de Aplicaciones Real",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Lee los permisos de las apps instaladas buscando combinaciones peligrosas (ej. Accesibilidad + Internet).",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    isScanning = true
                    coroutineScope.launch {
                        val scanResults = withContext(Dispatchers.IO) {
                            antivirus.scanInstalledApplications()
                        }
                        results = scanResults
                        isScanning = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                enabled = !isScanning
            ) {
                Text(if (isScanning) "Escaneando..." else "Escanear Dispositivo Ahora", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (isScanning) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00F0FF))
                }
            } else if (results != null) {
                if (results!!.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D24)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Safe", tint = Color(0xFF39FF14), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Dispositivo Limpio", color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
                            Text("No se detectaron aplicaciones con permisos críticos.", color = Color.White)
                        }
                    }
                } else {
                    Text(
                        text = "Amenazas Encontradas: ${results!!.size}",
                        color = Color(0xFFFF0055),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    LazyColumn {
                        items(results!!) { threat ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1E1E)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color(0xFFFF0055))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(threat.appName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(threat.packageName, color = Color.Gray, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    threat.detectedRisks.forEach { risk ->
                                        Text("• $risk", color = Color(0xFFFF4444), fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
""")
