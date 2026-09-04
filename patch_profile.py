import re

with open("app/src/main/java/com/securitynav/security/ui/ProfileActivity.kt", "r") as f:
    content = f.read()

new_effect = """
    // Cargar perfil en tiempo real
    DisposableEffect(user?.uid) {
        var listener: com.google.firebase.firestore.ListenerRegistration? = null
        if (user != null) {
            listener = db.collection("users").document(user.uid).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    userName = snapshot.getString("name") ?: ""
                    userRole = snapshot.getString("role") ?: "user"
                    profilePhotoUrl = snapshot.getString("photoUrl")
                }
                isLoading = false
            }
        } else {
            isLoading = false
        }
        onDispose {
            listener?.remove()
        }
    }
"""

content = re.sub(r'// Cargar perfil.*?LaunchedEffect.*?\}\s*\}\s*\}', new_effect.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/ui/ProfileActivity.kt", "w") as f:
    f.write(content)
