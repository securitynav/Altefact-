import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace(
    'applicationId = "com.securitynav.security"',
    'applicationId = "com.securitynav.app"'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
