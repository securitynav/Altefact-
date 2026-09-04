import re

with open("app/src/main/java/com/securitynav/security/ui/AuthActivity.kt", "r") as f:
    content = f.read()

# Fix it.uid
content = content.replace('db.collection("users").document(it.uid).update("fcmToken", token)', 'db.collection("users").document(user.uid).update("fcmToken", token)')

# Fix button content
content = content.replace('shape = RoundedCornerShape(8.dp)\n            )', 'shape = RoundedCornerShape(8.dp)\n            ) {\n                Text(if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión", color = Color.White)\n            }')

# Remove unused theme import
content = content.replace('import com.securitynav.security.ui.theme.SecurityNavTheme', '')

with open("app/src/main/java/com/securitynav/security/ui/AuthActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/securitynav/security/ui/SplashActivity.kt", "r") as f:
    content = f.read()

content = content.replace('val authManager = AuthManager(this)', '')

with open("app/src/main/java/com/securitynav/security/ui/SplashActivity.kt", "w") as f:
    f.write(content)
