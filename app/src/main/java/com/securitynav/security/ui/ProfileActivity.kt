package com.securitynav.security.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF0A131F),
                    surface = Color(0xFF1E2D3D)
                )
            ) {
                ProfileScreen(
                    onBack = { finish() },
                    onSignOut = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onSignOut: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val user = auth.currentUser
    val coroutineScope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("Cargando...") }
    var profilePhotoUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar perfil en tiempo real
    DisposableEffect(user?.uid) {
        var listener: com.google.firebase.firestore.ListenerRegistration? = null
        if (user != null) {
            listener = db.collection("users").document(user.uid).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    userName = snapshot.getString("name") ?: ""
                    userRole = snapshot.getString("role") ?: "user"
                    profilePhotoUrl = snapshot.getString("photoUrl")
                }
                isLoading = false
            }
        } else {
            isLoading = false
        }
        onDispose {
            listener?.remove()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && user != null) {
            isLoading = true
            val ref = storage.reference.child("profile_images/${user.uid}.jpg")
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    db.collection("users").document(user.uid)
                        .update("photoUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            profilePhotoUrl = downloadUri.toString()
                            isLoading = false
                            Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Error subiendo imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A131F))
            )
        },
        containerColor = Color(0xFF0A131F)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00F0FF))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2D3D)),
                contentAlignment = Alignment.Center
            ) {
                if (profilePhotoUrl != null) {
                    AsyncImage(
                        model = profilePhotoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Placeholder",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    imagePickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D3D))
            ) {
                Text("Cambiar Foto")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Información
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00F0FF),
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = user?.email ?: "",
                onValueChange = { },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Gray,
                    disabledBorderColor = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Rol: $userRole", color = Color(0xFF00F0FF), fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            Button(
                onClick = {
                    if (user != null) {
                        isLoading = true
                        db.collection("users").document(user.uid)
                            .update("name", userName)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar Cambios")
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            isLoading = true
                            user?.let {
                                db.collection("users").document(it.uid).delete().await()
                                it.delete().await()
                            }
                            onSignOut()
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error eliminando cuenta. Requiere login reciente.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD93025)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Eliminar Cuenta")
            }
        }
    }
}
