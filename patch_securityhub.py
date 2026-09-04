import re

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "r") as f:
    content = f.read()

imports = """
import androidx.compose.runtime.collectAsState
import com.securitynav.security.billing.SubscriptionManager
import com.securitynav.security.billing.PaywallBottomSheet
"""

content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\n" + imports)

state_vars = """
    var hardwareVault by remember { mutableStateOf(false) }

    val isPro by SubscriptionManager.isProUser.collectAsState(initial = false)
    var showPaywall by remember { mutableStateOf(false) }

    if (showPaywall) {
        PaywallBottomSheet(
            onDismiss = { showPaywall = false },
            subscriptionManager = SubscriptionManager
        )
    }
"""

content = content.replace("var hardwareVault by remember { mutableStateOf(false) }", state_vars.strip())

# Replace Master Lock clickable
master_lock_old = """
                    .clickable { 
                         isMasterArmed = !isMasterArmed
                        if (isMasterArmed) {
                            heuristicScanner = true
                            towerGuard = true
                            accessibilityShield = true
                            overlayGuard = true
                            netGuardDpi = true
                            appProcessDpi = true
                            hardwareVault = true
                        } else {
                            heuristicScanner = false
                            towerGuard = false
                            accessibilityShield = false
                            overlayGuard = false
                            netGuardDpi = false
                            appProcessDpi = false
                            hardwareVault = false
                        }
                    }
"""

master_lock_new = """
                    .clickable { 
                         isMasterArmed = !isMasterArmed
                         val turnOn = isMasterArmed
                         
                         heuristicScanner = turnOn
                         accessibilityShield = turnOn
                         overlayGuard = turnOn
                         hardwareVault = turnOn
                         
                         if (turnOn) {
                             context.startService(Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java))
                         } else {
                             context.stopService(Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java))
                         }
                         
                         if (isPro || !turnOn) {
                             towerGuard = turnOn
                             netGuardDpi = turnOn
                             appProcessDpi = turnOn
                             if (turnOn) {
                                 context.startService(Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java))
                                 val intent = Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java)
                                 intent.putExtra("monitor_only", false)
                                 context.startService(intent)
                             } else {
                                 context.stopService(Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java))
                                 context.stopService(Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java))
                             }
                         } else if (turnOn) {
                             showPaywall = true
                         }
                    }
"""

content = content.replace(master_lock_old.strip(), master_lock_new.strip())

# Replace individual switches
switches_old = """
                    NodeSwitchItem("1. Heuristic Engine", "Análisis de malware local", heuristicScanner) { heuristicScanner = it }
                    NodeSwitchItem("2. Tower Guard", "Anti-Rastreo Celular (IMSI)", towerGuard) { 
                         towerGuard = it 
                         val intent = Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("3. Accessibility Shield", "Protección de Accesibilidad", accessibilityShield) { 
                         accessibilityShield = it
                        if (it) {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                    NodeSwitchItem("4. Overlay Guard", "Anti-Tapjacking", overlayGuard) { 
                         overlayGuard = it 
                         val intent = Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("5. NetGuard DPI", "VPN Local & DNS Cifrado", netGuardDpi) { 
                         netGuardDpi = it 
                         val intent = Intent(context, com.securitynav.security.vpn.LocalVpnService::class.java)
                        intent.putExtra("monitor_only", !it)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
                    NodeSwitchItem("6. App Process DPI", "Análisis por Proceso", appProcessDpi) { appProcessDpi = it }
                    NodeSwitchItem("7. Hardware Storage Vault", "AES-256-GCM + SQLCipher", hardwareVault) { hardwareVault = it }
"""

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
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
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

content = content.replace(switches_old.strip(), switches_new.strip())

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "w") as f:
    f.write(content)

