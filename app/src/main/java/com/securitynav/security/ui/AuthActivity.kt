package com.securitynav.security.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

import com.securitynav.security.util.NetworkUtils // Adjust theme if necessary

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF0A131F),
                    surface = Color(0xFF1E2D3D)
                )
            ) {
                AuthScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A131F))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF00F0FF),
                unfocusedBorderColor = Color.Gray
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
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

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF00F0FF))
        } else {
            Button(
                onClick = {
                    if (!NetworkUtils.isInternetAvailable(context)) {
                        Toast.makeText(context, "Sin conexión a Internet", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Llene todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    if (isRegisterMode) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val profile = hashMapOf(
                                        "email" to email,
                                        "role" to "user",
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                    user?.let {
                                        db.collection("users").document(it.uid)
                                            .set(profile)
                                            .addOnSuccessListener {
                                                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                                    db.collection("users").document(user.uid).update("fcmToken", token)
                                                }
                                                user.sendEmailVerification()
                                                Toast.makeText(context, "Cuenta creada. Verifica tu correo.", Toast.LENGTH_LONG).show()
                                                isLoading = false
                                                onLoginSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                Toast.makeText(context, "Error guardando perfil: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                            db.collection("users").document(user.uid).update("fcmToken", token)
                                        }
                                    }
                                    onLoginSuccess()

                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión", color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRegisterMode) "¿Ya tienes cuenta? Inicia Sesión" else "¿No tienes cuenta? Regístrate",
                color = Color(0xFF00F0FF),
                modifier = Modifier.clickable { isRegisterMode = !isRegisterMode }
            )
            
            if (!isRegisterMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        if (email.isEmpty()) {
                            Toast.makeText(context, "Ingresa tu correo primero", Toast.LENGTH_SHORT).show()
                        } else {
                            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                                if (it.isSuccessful) Toast.makeText(context, "Correo enviado", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}
