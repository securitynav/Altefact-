package com.securitynav.security.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.securitynav.security.engine.CellAnomalyDetector
import com.securitynav.security.engine.RealCellData
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class CellTowerFragment : Fragment() {

    private lateinit var anomalyDetector: CellAnomalyDetector

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Toast.makeText(context, "Permisos otorgados", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Se requiere permiso de ubicación para escanear antenas", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        anomalyDetector = CellAnomalyDetector(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    RealCellScannerScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.any { ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED }) {
            permissionLauncher.launch(permissions)
        }
    }

    @Composable
    fun RealCellScannerScreen() {
        var cellList by remember { mutableStateOf(listOf<RealCellData>()) }
        var isDowngrade by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            while (isActive) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    cellList = anomalyDetector.getRealCellData()
                    isDowngrade = anomalyDetector.isDangerousDowngrade()
                }
                delay(2000)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A131F))
                .padding(16.dp)
        ) {
            Text(
                text = "Real Cell Tower Scanner",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isDowngrade) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1E1E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color(0xFFFF0055))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ALERTA: Reduciendo cifrado a 2G/GSM detectado (Posible ataque Stingray).", color = Color(0xFFFF0055))
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D24)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "OK", tint = Color(0xFF39FF14))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cifrado de red celular seguro (4G/5G).", color = Color(0xFF39FF14))
                    }
                }
            }

            if (cellList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Escaneando antenas reales... (O permiso denegado)", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(cellList) { cell ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16202D)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("ID de Antena (Cell ID): ${cell.cellId}", color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Tecnología: ${cell.type}", color = if (cell.type.contains("2G")) Color(0xFFFF0055) else Color(0xFF00F0FF))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Señal (dBm): ${cell.signalStrength}", color = Color.LightGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(if (cell.isRegistered) "Estado: Conectado" else "Estado: Visible / Vecino", color = if (cell.isRegistered) Color(0xFF39FF14) else Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
