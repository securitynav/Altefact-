with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "r") as f:
    content = f.read()

import re

# Remove simulate method
content = re.sub(r'fun generateRandomTrafficLog\(\): TrafficLog \{.*?\n\}', '', content, flags=re.DOTALL)

# Add imports
imports = """
import com.securitynav.security.engine.PacketAnalyzer
import com.securitynav.security.engine.RealTrafficLog
"""
content = content.replace("import androidx.fragment.app.Fragment", "import androidx.fragment.app.Fragment\n" + imports)

# Remove the data class TrafficLog
content = re.sub(r'data class TrafficLog\(.*?\n\)\n', '', content, flags=re.DOTALL)


# Fix the state flow observation
# Remove: var trafficLogs by remember { mutableStateOf(listOf<TrafficLog>()) }
content = re.sub(r'var trafficLogs by remember \{ mutableStateOf\(listOf<TrafficLog>\(\)\) \}', 'val trafficLogs by PacketAnalyzer.recentPackets.collectAsState(initial = emptyList())', content)

# Remove the simulated generation logic inside LaunchedEffect
#              if (isMasterArmed && Math.random() > 0.6) {
#                 val newLog = generateRandomTrafficLog()
#                 trafficLogs = listOf(newLog) + trafficLogs.take(15)
#             }

content = re.sub(r'if \(isMasterArmed && Math\.random\(\) > 0\.6\) \{.*?\}', '', content, flags=re.DOTALL)

# Update TrafficLogItem argument
content = content.replace('fun TrafficLogItem(log: TrafficLog)', 'fun TrafficLogItem(log: RealTrafficLog)')

# In TrafficLogItem, use log.sourceIp / destinationIp / payloadSize, etc.
# Update the UI strings 
# We need to map RealTrafficLog to the UI strings we were using.
# isOutbound -> log.isOutbound
# protocol -> log.protocol
# port -> log.port
# appName -> log.destinationIp (or source if inbound)
# destination -> log.destinationIp
# payloadSize -> "${log.payloadSize} B"
# encryption -> if(port == 443) "TLS" else "Unknown"
# status -> "INSPECTADO"

traffic_log_item_code = """
@Composable
fun TrafficLogItem(log: RealTrafficLog) {
    var expanded by remember { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.secondary
    
    val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timestampStr = df.format(Date(log.timestamp))
    
    val targetIp = if (log.isOutbound) log.destinationIp else log.sourceIp
    val statusStr = if (log.port == 443) "SEGURO" else "ANALIZADO"
    val encryption = if (log.port == 443) "TLS/HTTPS" else "No Cifrado/Desconocido"
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

content = re.sub(r'@Composable\nfun TrafficLogItem\(log: RealTrafficLog\).*?\}\n\}\n\}', traffic_log_item_code, content, flags=re.DOTALL)


with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "w") as f:
    f.write(content)
