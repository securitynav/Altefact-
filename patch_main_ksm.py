import re

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import com.securitynav.security.crypto.KeyStoreManager", "import com.securitynav.security.data.security.KeyStoreManager")

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "w") as f:
    f.write(content)
