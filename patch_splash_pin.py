import re

with open("app/src/main/java/com/securitynav/security/ui/SplashActivity.kt", "r") as f:
    content = f.read()

content = content.replace("startActivity(Intent(this, MainActivity::class.java))", "startActivity(Intent(this, PinActivity::class.java))")

with open("app/src/main/java/com/securitynav/security/ui/SplashActivity.kt", "w") as f:
    f.write(content)
