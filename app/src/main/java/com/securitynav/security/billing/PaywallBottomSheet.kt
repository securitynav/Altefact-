package com.securitynav.security.billing

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallBottomSheet(
    onDismiss: () -> Unit,
    subscriptionManager: SubscriptionManager = SubscriptionManager
) {
    val context = LocalContext.current
    var selectedTier by remember { mutableStateOf(SubscriptionTier.PRO_5USD) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0A131F),
        scrimColor = Color.Black.copy(alpha = 0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Desbloquea Protección Máxima",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Plan Pro
            SubscriptionCard(
                title = "Plan Pro (Advanced)",
                price = "$5.00 / mes",
                features = listOf(
                    "Tower Guard (Anti-2G/IMSI Catchers)",
                    "Accessibility & Overlay Shield",
                    "Visualización de Tráfico por App en tiempo real",
                    "Servidor DNS cifrado (DoH)"
                ),
                isSelected = selectedTier == SubscriptionTier.PRO_5USD,
                onClick = { selectedTier = SubscriptionTier.PRO_5USD },
                accentColor = Color(0xFF00F0FF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Plan Ultimate
            SubscriptionCard(
                title = "Plan Ultimate (Forensic)",
                price = "$10.00 / mes",
                features = listOf(
                    "Todo lo del Plan Pro",
                    "Inspección profunda de Payloads (DPI)",
                    "Exportación de reportes forenses cifrados",
                    "Bóveda Falsa (PIN de Coacción)"
                ),
                isSelected = selectedTier == SubscriptionTier.ULTIMATE_10USD,
                onClick = { selectedTier = SubscriptionTier.ULTIMATE_10USD },
                accentColor = Color(0xFFFFB300)
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Método de Pago", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PaymentButton("Google Play", Icons.Default.CheckCircle, onClick = {
                    subscriptionManager.setActiveTier(selectedTier)
                    onDismiss()
                })
                PaymentButton("PayPal", Icons.Default.Payment, onClick = {
                    // Abrir Checkout de PayPal en Render
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://securitynav-service.onrender.com/api/v1/payments/checkout-session?method=paypal&tier=${selectedTier.name}"))
                    context.startActivity(intent)
                    onDismiss()
                })
                PaymentButton("Tarjeta", Icons.Default.Security, onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://securitynav-service.onrender.com/api/v1/payments/checkout-session?method=card&tier=${selectedTier.name}"))
                    context.startActivity(intent)
                    onDismiss()
                })
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SubscriptionCard(
    title: String,
    price: String,
    features: List<String>,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF112330) else Color(0xFF1E2D3D)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) accentColor else Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun RowScope.PaymentButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D3D), contentColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
            .height(50.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text, fontSize = 10.sp)
        }
    }
}
