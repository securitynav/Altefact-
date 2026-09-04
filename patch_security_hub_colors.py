import re

with open('app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt', 'r') as f:
    content = f.read()

# Replace hardcoded colors with MaterialTheme properties or dynamic colors
content = content.replace('Color(0xFF0A131F)', 'MaterialTheme.colorScheme.background')
content = content.replace('Color(0xFF1E2D3D)', 'MaterialTheme.colorScheme.surface')
content = content.replace('Color(0xFF112330)', 'MaterialTheme.colorScheme.surface')
content = content.replace('Color(0xFF00FFCC)', 'MaterialTheme.colorScheme.primary')
content = content.replace('Color(0xFF00F0FF)', 'MaterialTheme.colorScheme.primary')
content = content.replace('Color(0xFFFF0055)', 'MaterialTheme.colorScheme.error')
content = content.replace('Color(0xFF051525)', 'MaterialTheme.colorScheme.background')
content = content.replace('Color.White', 'MaterialTheme.colorScheme.onSurface')
content = content.replace('Color.Gray', 'MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)')
content = content.replace('Color.Transparent', 'androidx.compose.ui.graphics.Color.Transparent')

with open('app/src/main/java/com/securitynav/security/ui/SecurityHubFragment.kt', 'w') as f:
    f.write(content)
