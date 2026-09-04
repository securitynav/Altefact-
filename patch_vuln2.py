import re

with open("app/src/main/java/com/securitynav/security/ui/VulnerabilityDashboardFragment.kt", "r") as f:
    content = f.read()

content = content.replace("LazyColumn((", "LazyColumn(")

with open("app/src/main/java/com/securitynav/security/ui/VulnerabilityDashboardFragment.kt", "w") as f:
    f.write(content)
