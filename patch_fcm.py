import re

with open("app/src/main/java/com/securitynav/security/notifications/SecurityFirebaseMessagingService.kt", "r") as f:
    content = f.read()

imports = """
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
"""
content = content.replace("import com.securitynav.security.ui.MainActivity", "import com.securitynav.security.ui.MainActivity\n" + imports)

new_token = """
    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .update("fcmToken", token)
        }
    }
"""

content = re.sub(r'override fun onNewToken.*?// Normally send token to server\s*\}', new_token.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/notifications/SecurityFirebaseMessagingService.kt", "w") as f:
    f.write(content)

