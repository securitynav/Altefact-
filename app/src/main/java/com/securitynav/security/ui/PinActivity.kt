package com.securitynav.security.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.securitynav.security.data.security.KeyStoreManager

class PinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF0A131F),
                    surface = Color(0xFF1E2D3D)
                )
            ) {
                PinScreen(
                    onPinSuccess = {
                        // Re-initialize KeyStore if needed
                        try {
                            KeyStoreManager(this).getMasterPassphrase()
                        } catch (e: Exception) {}
                        
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this, AuthActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PinScreen(onPinSuccess: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("security_nav_prefs", Context.MODE_PRIVATE)
    val savedPin = prefs.getString("user_pin", null)
    
    var pin by remember { mutableStateOf("") }
    val isSettingPin = savedPin == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A131F))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSettingPin) "Configure su PIN de 6 dígitos" else "Ingrese su PIN de Seguridad",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 6) pin = it },
            label = { Text("PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF00F0FF),
                unfocusedBorderColor = Color.Gray
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (pin.length != 6) {
                    Toast.makeText(context, "El PIN debe tener 6 dígitos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                if (isSettingPin) {
                    prefs.edit().putString("user_pin", pin).apply()
                    Toast.makeText(context, "PIN configurado", Toast.LENGTH_SHORT).show()
                    onPinSuccess()
                } else {
                    if (pin == savedPin) {
                        onPinSuccess()
                    } else {
                        Toast.makeText(context, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
                        pin = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Ingresar", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = onLogout) {
            Text("Cerrar Sesión", color = Color.Gray)
        }
    }
}
