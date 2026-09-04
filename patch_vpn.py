import re

with open("app/src/main/java/com/securitynav/security/vpn/LocalVpnService.kt", "r") as f:
    content = f.read()

# Add a critical app to the sources for the demo
content = content.replace(
    'val sources = listOf("com.unknown.app", "Cell ID: 9942", "DNS: 8.8.4.4", "Accessibility Overlay")',
    'val sources = listOf("WhatsApp", "com.unknown.app", "Cell ID: 9942", "DNS: 8.8.4.4", "Accessibility Overlay")'
)

# Start CriticalAppMonitor in onCreate
content = content.replace(
    'dbHelper = SecurityDatabaseHelper(this)',
    'dbHelper = SecurityDatabaseHelper(this)\n        com.securitynav.security.monitor.CriticalAppMonitor.startMonitoring(this)'
)

with open("app/src/main/java/com/securitynav/security/vpn/LocalVpnService.kt", "w") as f:
    f.write(content)
