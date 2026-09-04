with open("app/src/main/java/com/securitynav/security/vpn/LocalVpnService.kt", "r") as f:
    content = f.read()

# Replace simulateSecurityEvent call with real packet processing
real_packet_code = """
                        if (!isLoopback && isValidated) {
                            NetworkMonitor.recordInbound(read.toLong())
                            
                            try {
                                if (read >= 20 && (buffer[0].toInt() shr 4) == 4) { // IPv4
                                    val ihl = buffer[0].toInt() and 0x0F
                                    val ipHeaderLength = ihl * 4
                                    val protocol = buffer[9].toInt() and 0xFF
                                    
                                    val srcIp = "${buffer[12].toInt() and 0xFF}.${buffer[13].toInt() and 0xFF}.${buffer[14].toInt() and 0xFF}.${buffer[15].toInt() and 0xFF}"
                                    val destIp = "${buffer[16].toInt() and 0xFF}.${buffer[17].toInt() and 0xFF}.${buffer[18].toInt() and 0xFF}.${buffer[19].toInt() and 0xFF}"
                                    
                                    var srcPort = 0
                                    var destPort = 0
                                    var protoStr = "OTHER"
                                    
                                    if (protocol == 6 && read >= ipHeaderLength + 4) { // TCP
                                        protoStr = "TCP"
                                        srcPort = ((buffer[ipHeaderLength].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 1].toInt() and 0xFF)
                                        destPort = ((buffer[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 3].toInt() and 0xFF)
                                    } else if (protocol == 17 && read >= ipHeaderLength + 4) { // UDP
                                        protoStr = "UDP"
                                        srcPort = ((buffer[ipHeaderLength].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 1].toInt() and 0xFF)
                                        destPort = ((buffer[ipHeaderLength + 2].toInt() and 0xFF) shl 8) or (buffer[ipHeaderLength + 3].toInt() and 0xFF)
                                    }
                                    
                                    val isOutbound = srcIp == "10.0.0.2"
                                    
                                    com.securitynav.security.engine.PacketAnalyzer.addPacket(
                                        com.securitynav.security.engine.RealTrafficLog(
                                            isOutbound = isOutbound,
                                            protocol = protoStr,
                                            port = if (isOutbound) destPort else srcPort,
                                            sourceIp = srcIp,
                                            destinationIp = destIp,
                                            payloadSize = read,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                // Ignore parse errors
                            }
                        }
"""

content = content.replace("""                        if (!isLoopback && isValidated) {
                            NetworkMonitor.recordInbound(read.toLong())
                            
                            // Simulate security event analysis every ~50 packets
                            packetCount++
                            if (packetCount > 50) {
                                packetCount = 0
                                if (Random.nextFloat() < 0.3f) { // 30% chance per ~50 packets
                                    simulateSecurityEvent()
                                }
                            }
                        }""", real_packet_code)

# Let's completely remove the simulateSecurityEvent method so it's not even defined.
import re
content = re.sub(r'private fun simulateSecurityEvent\(\) \{.*?\n    \}', '', content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/vpn/LocalVpnService.kt", "w") as f:
    f.write(content)

