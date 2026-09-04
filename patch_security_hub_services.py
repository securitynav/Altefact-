import re

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "r") as f:
    content = f.read()

patch = """
                    NodeSwitchItem("2. Tower Guard", "Anti-Rastreo Celular (IMSI)", towerGuard) { 
                        towerGuard = it 
                        val intent = Intent(context, com.securitynav.security.services.CellularSabotageDetectionService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
"""

content = re.sub(
    r'NodeSwitchItem\("2\. Tower Guard", "Anti-Rastreo Celular \(IMSI\)", towerGuard\) \{ towerGuard = it \}',
    patch.strip(),
    content
)

patch2 = """
                    NodeSwitchItem("4. Overlay Guard", "Anti-Tapjacking", overlayGuard) { 
                        overlayGuard = it 
                        val intent = Intent(context, com.securitynav.security.services.ScreenSpyAuditService::class.java)
                        if (it) context.startService(intent) else context.stopService(intent)
                    }
"""

content = re.sub(
    r'NodeSwitchItem\("4\. Overlay Guard", "Anti-Tapjacking", overlayGuard\) \{ overlayGuard = it \}',
    patch2.strip(),
    content
)

with open("app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt", "w") as f:
    f.write(content)
