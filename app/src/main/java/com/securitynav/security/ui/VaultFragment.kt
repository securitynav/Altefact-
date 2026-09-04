package com.securitynav.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.securitynav.security.engine.VaultManager
import com.securitynav.security.engine.ProtectedApp
import androidx.compose.material.icons.filled.Warning

class VaultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme(
                        background = Color(0xFF0A131F),
                        surface = Color(0xFF1E2D3D)
                    )
                ) {
                    VaultScreen()
                }
            }
        }
    }
}


@Composable
fun VaultScreen() {
    var isUnlocked by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A131F))) {
        if (!isUnlocked) {
            VaultLockScreen(onUnlock = { isUnlocked = true })
        } else {
            VaultDashboardScreen(onLock = { isUnlocked = false })
        }
    }
}

@Composable
fun VaultLockScreen(onUnlock: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Shield,
            contentDescription = "Bóveda",
            tint = Color(0xFF00F0FF),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "BÓVEDA CIFRADA",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Ingresa tu PIN maestro para acceder (PIN: 1234)",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )
        
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (pin.length > i) Color(0xFF00F0FF) else Color(0xFF1E2D3D))
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Keypad
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (row in keys.chunked(3)) {
                Row(modifier = Modifier.padding(bottom = 16.dp)) {
                    for (key in row) {
                        Button(
                            onClick = {
                                if (key == "C") pin = ""
                                else if (key == "OK") {
                                    if (pin == "1234") onUnlock() // Default PIN for demo
                                    else pin = ""
                                }
                                else if (pin.length < 4) pin += key
                                
                                if (pin.length == 4 && key != "C" && key != "OK") {
                                    if (pin == "1234") onUnlock()
                                    else pin = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E2D3D),
                                contentColor = Color(0xFF00F0FF)
                            ),
                            shape = CircleShape,
                            modifier = Modifier.padding(horizontal = 12.dp).size(70.dp)
                        ) {
                            Text(text = key, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultDashboardScreen(onLock: () -> Unit) {
    val apps by VaultManager.apps.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BÓVEDA CIFRADA",
                    color = Color(0xFF00F0FF),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "AES-256 Nivel Militar",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            IconButton(
                onClick = onLock,
                modifier = Modifier.background(Color(0xFF1E2D3D), CircleShape)
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Bloquear", tint = Color(0xFFFF0055))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D3D)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF00F0FF))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("App Locker Activo", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Selecciona qué aplicaciones requieren autenticación adicional (PIN o Biometría) antes de poder abrirlas.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
        
        Text(
            text = "APLICACIONES INSTALADAS",
            color = Color(0xFF00F0FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(apps) { app ->
                AppProtectionItem(app = app, onToggle = { isChecked ->
                    VaultManager.toggleProtection(app.id, isChecked)
                })
            }
        }
    }
}


@Composable
fun AppProtectionItem(app: ProtectedApp, onToggle: (Boolean) -> Unit) {
    val cardColor = if (app.isTerminated) Color(0xFF2A0A10) else if (app.isProtected) Color(0xFF152233) else Color(0xFF1E2D3D)
    val borderColor = if (app.isTerminated) Color(0xFFFF0055) else if (app.isProtected) Color(0xFF00F0FF) else Color.Transparent
    val iconVector = when(app.iconType) {
        "message" -> Icons.Default.Message
        "photo" -> Icons.Default.Photo
        "bank" -> Icons.Default.AccountBalance
        "email" -> Icons.Default.Email
        else -> Icons.Default.Apps
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).background(Color(0xFF0A131F), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            iconVector, 
                            contentDescription = null, 
                            tint = if (app.isTerminated) Color(0xFFFF0055) else if (app.isProtected) Color(0xFF00F0FF) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = app.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (app.isTerminated) {
                            Text(text = "TERMINADA POR SISTEMA", color = Color(0xFFFF0055), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = if (app.isProtected) "Protegida por Bóveda" else "Sin protección", 
                                color = if (app.isProtected) Color(0xFF00F0FF) else Color.Gray, 
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Switch(
                    checked = app.isProtected,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0A131F),
                        checkedTrackColor = if (app.isTerminated) Color(0xFFFF0055) else Color(0xFF00F0FF),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF0A131F)
                    )
                )
            }
            
            // Critical app section
            if (app.isProtected) {
                Spacer(modifier = Modifier.height(12.dp))
                androidx.compose.material3.Divider(color = Color.DarkGray, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Designar como App Crítica", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Checkbox(
                        checked = app.isCritical,
                        onCheckedChange = { isCrit ->
                            VaultManager.toggleCritical(app.id, isCrit)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFB300),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color(0xFF0A131F)
                        )
                    )
                }
                if (app.isCritical) {
                    Text(
                        text = "Si DataLeakDetector detecta transmisiones sin cifrar, el sistema cerrará esta app automáticamente.",
                        color = Color(0xFFFFB300),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 24.dp)
                    )
                }
            }
        }
    }
}

