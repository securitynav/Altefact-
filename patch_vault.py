import re

with open("app/src/main/java/com/securitynav/security/ui/VaultFragment.kt", "r") as f:
    content = f.read()

# Make sure imports are present
if "import com.securitynav.security.engine.VaultManager" not in content:
    content = content.replace("import androidx.fragment.app.Fragment", "import androidx.fragment.app.Fragment\nimport com.securitynav.security.engine.VaultManager\nimport com.securitynav.security.engine.ProtectedApp\nimport androidx.compose.material.icons.filled.Warning")

# Remove local ProtectedApp definition
content = re.sub(r'data class ProtectedApp.*?\n', '', content)

# Change apps state to use VaultManager
content = re.sub(
    r'var apps by remember \{.*?mutableStateOf\(.*?listOf\(.*?\)[\s\S]*?\}',
    'val apps by VaultManager.apps.collectAsState()',
    content,
    flags=re.DOTALL
)

# Replace the toggle logic
content = content.replace(
    'apps = apps.map { if (it.id == app.id) it.copy(isProtected = isChecked) else it }',
    'VaultManager.toggleProtection(app.id, isChecked)'
)

# We need to add the Critical App toggle UI inside AppProtectionItem.
# Since we are completely replacing AppProtectionItem, let's do this:

item_code = """
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
"""

content = re.sub(r'@Composable\nfun AppProtectionItem.*?}$', item_code, content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/ui/VaultFragment.kt", "w") as f:
    f.write(content)

