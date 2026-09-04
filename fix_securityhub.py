import re

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "r") as f:
    content = f.read()

# Replace Master Lock clickable
master_lock_new = """
                    .clickable { 
                         val turnOn = !isMasterArmed
                         
                         if (turnOn) {
                             if (isPro) {
                                 isMasterArmed = true
                                 heuristicScanner = true
                                 accessibilityShield = true
                                 overlayGuard = true
                                 hardwareVault = true
                                 towerGuard = true
                                 netGuardDpi = true
                                 appProcessDpi = true
                                 
                                 context.startService(Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java))
                                 context.startService(Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java))
                                 val intent = Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java)
                                 intent.putExtra("monitor_only", false)
                                 context.startService(intent)
                             } else {
                                 showPaywall = true
                             }
                         } else {
                             isMasterArmed = false
                             heuristicScanner = false
                             towerGuard = false
                             accessibilityShield = false
                             overlayGuard = false
                             netGuardDpi = false
                             appProcessDpi = false
                             hardwareVault = false
                             
                             context.stopService(Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java))
                             context.stopService(Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java))
                             context.stopService(Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java))
                         }
                    }
"""

content = re.sub(r'\.clickable\s*\{\s*isMasterArmed = !isMasterArmed\s*if \(isMasterArmed\) \{.*?hardwareVault = false\s*\}\s*\}', master_lock_new.strip(), content, flags=re.DOTALL)

# Replace Switch items
switches_old_regex = r'NodeSwitchItem\("1\. Heuristic Engine".*?hardwareVault = it \}'

switches_new = """
                    NodeSwitchItem("1. Heuristic Engine", "Análisis de malware local", heuristicScanner) { heuristicScanner = it }
                    NodeSwitchItem("2. Tower Guard", "Anti-Rastreo Celular (IMSI) - PRO", towerGuard) { 
                         if (!isPro && it) { showPaywall = true; return@NodeSwitchItem }
                         towerGuard = it 
                         val intent = Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("3. Accessibility Shield", "Protección de Accesibilidad", accessibilityShield) { 
                         accessibilityShield = it
                        if (it) {
                            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                    NodeSwitchItem("4. Overlay Guard", "Anti-Tapjacking", overlayGuard) { 
                         overlayGuard = it 
                         val intent = Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("5. NetGuard DPI", "VPN Local & DNS Cifrado - PRO", netGuardDpi) { 
                         if (!isPro && it) { showPaywall = true; return@NodeSwitchItem }
                         netGuardDpi = it 
                         val intent = Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java)
                        intent.putExtra("monitor_only", !it)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("6. App Process DPI", "Análisis por Proceso - PRO", appProcessDpi) { 
                        if (!isPro && it) { showPaywall = true; return@NodeSwitchItem }
                        appProcessDpi = it 
                    }
                    NodeSwitchItem("7. Hardware Storage Vault", "AES-256-GCM + SQLCipher", hardwareVault) { hardwareVault = it }
"""

content = re.sub(switches_old_regex, switches_new.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "w") as f:
    f.write(content)
