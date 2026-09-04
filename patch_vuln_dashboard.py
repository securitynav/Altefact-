import re

with open('app/src/main/java/com/securitynav/security/ui/VulnerabilityDashboardFragment.kt', 'r') as f:
    content = f.read()

# Replace the hardcoded MaterialTheme
content = re.sub(r'MaterialTheme\(\s*colorScheme = lightColorScheme\([^)]+\)\s*\)', 'MaterialTheme', content, flags=re.MULTILINE)
content = content.replace('Color.White', 'MaterialTheme.colorScheme.background')

with open('app/src/main/java/com/securitynav/security/ui/VulnerabilityDashboardFragment.kt', 'w') as f:
    f.write(content)
