import re

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "r") as f:
    content = f.read()

# I missed removing some references to the old TrafficLog variables like `appName`, `destination`, `encryption`, `insights`, `status`.
# The traffic_log_item_code I injected actually contains some errors if I didn't replace the original TrafficLogItem correctly. Let's see what is inside TrafficLogItem now.
# Wait, I did `re.sub(r'@Composable\nfun TrafficLogItem\(log: RealTrafficLog\).*?\}\n\}\n\}', ...)` which might have failed to replace if the old signature was `TrafficLog`.
# Oh! In the previous script I did: `content = content.replace('fun TrafficLogItem(log: TrafficLog)', 'fun TrafficLogItem(log: RealTrafficLog)')` before the regex.
# Let's just completely replace TrafficLogItem.

new_item = """
@Composable
fun TrafficLogItem(log: RealTrafficLog) {
    var expanded by remember { mutableStateOf(false) }
    
    val df = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
    val timestampStr = df.format(java.util.Date(log.timestamp))
    
    val targetIp = if (log.isOutbound) log.destinationIp else log.sourceIp
    val isSecure = log.port == 443
    val color = if (isSecure) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    val statusStr = if (isSecure) "SEGURO" else "ANALIZADO"
    val encryption = if (isSecure) "TLS/HTTPS" else "No Cifrado/Desconocido"
    val insights = "Paquete IP interceptado y analizado por DPI Local."
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (log.isOutbound) "⬆ SALIENTE" else "⬇ ENTRANTE", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(timestampStr, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f), fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(targetIp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Puerto: ${log.port}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Protocolo: ${log.protocol}", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), fontSize = 12.sp)
                        Text("Cifrado: ${encryption}", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tamaño: ${log.payloadSize} B", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), fontSize = 12.sp)
                        Text("Estado: ${statusStr}", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Análisis DPI:", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(insights, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }
        }
    }
}
"""

# Find the start of TrafficLogItem and end of it.
content = re.sub(r'@Composable\nfun TrafficLogItem\(log: RealTrafficLog\).*?(?=@Composable\nfun MarineRadar)', new_item + '\n', content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "w") as f:
    f.write(content)

