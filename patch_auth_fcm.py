import re

with open("app/src/main/java/com/securitynav/security/ui/AuthActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import com.google.firebase.firestore.FirebaseFirestore", "import com.google.firebase.firestore.FirebaseFirestore\nimport com.google.firebase.messaging.FirebaseMessaging")

fcm_logic = """
                                                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                                    db.collection("users").document(it.uid).update("fcmToken", token)
                                                }
                                                user.sendEmailVerification()
"""
content = content.replace("user.sendEmailVerification()", fcm_logic.strip())

fcm_login_logic = """
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                            db.collection("users").document(user.uid).update("fcmToken", token)
                                        }
                                    }
                                    onLoginSuccess()
"""
content = content.replace("if (task.isSuccessful) {\n                                    onLoginSuccess()", fcm_login_logic)

with open("app/src/main/java/com/securitynav/security/ui/AuthActivity.kt", "w") as f:
    f.write(content)
