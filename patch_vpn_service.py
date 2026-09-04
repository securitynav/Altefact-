import re

with open("app/src/main/java/com/securitynav/security/vpn/LocalVpnService.kt", "r") as f:
    content = f.read()

# Add imports for ConnectivityManager
content = content.replace("import android.net.VpnService", "import android.net.VpnService\nimport android.net.ConnectivityManager\nimport android.net.NetworkCapabilities")

# In runVpnLoop:
patch = """
                    val read = withContext(Dispatchers.IO) { 
                        input.read(buffer) 
                    }
                    
                    if (read > 0) {
                        // Check for loopback (127.x.x.x)
                        var isLoopback = false
                        if (read >= 20 && (buffer[0].toInt() shr 4) == 4) { // IPv4
                            val srcIp1 = buffer[12].toInt() and 0xFF
                            val destIp1 = buffer[16].toInt() and 0xFF
                            if (srcIp1 == 127 || destIp1 == 127) {
                                isLoopback = true
                            }
                        }
                        
                        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork = cm.activeNetwork
                        val caps = cm.getNetworkCapabilities(activeNetwork)
                        val isValidated = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
                        
                        if (!isLoopback && isValidated) {
                            NetworkMonitor.recordInbound(read.toLong())
                            
                            // Simulate security event analysis every ~50 packets
                            packetCount++
                            if (packetCount > 50) {
                                packetCount = 0
                                if (Random.nextFloat() < 0.3f) { // 30% chance per ~50 packets
                                    simulateSecurityEvent()
                                }
                            }
                        }

                        withContext(Dispatchers.IO) { 
                            output.write(buffer, 0, read) 
                        }
                        
                        if (!isLoopback && isValidated) {
                            NetworkMonitor.recordOutbound(read.toLong())
                        }
"""

content = re.sub(
    r'val read = withContext\(Dispatchers\.IO\) \{ \n\s*input\.read\(buffer\) \n\s*\}\n\s*if \(read > 0\) \{\n\s*NetworkMonitor\.recordInbound\(read\.toLong\(\)\)\n\s*// Simulate security event analysis every ~50 packets\n\s*packetCount\+\+\n\s*if \(packetCount > 50\) \{\n\s*packetCount = 0\n\s*if \(Random\.nextFloat\(\) < 0\.3f\) \{ // 30% chance per ~50 packets\n\s*simulateSecurityEvent\(\)\n\s*\}\n\s*\}\n\s*withContext\(Dispatchers\.IO\) \{ \n\s*output\.write\(buffer, 0, read\) \n\s*\}\n\s*NetworkMonitor\.recordOutbound\(read\.toLong\(\)\)',
    patch.strip(),
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/securitynav/security/vpn/LocalVpnService.kt", "w") as f:
    f.write(content)
