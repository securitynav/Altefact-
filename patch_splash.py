import re

with open("app/src/main/java/com/securitynav/security/ui/SplashActivity.kt", "r") as f:
    content = f.read()

imports = """
import com.google.firebase.auth.FirebaseAuth
"""

content = content.replace("import com.securitynav.security.data.AuthManager", imports)

new_logic = """
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        }, 2200)
"""

content = re.sub(r'Handler\(Looper\.getMainLooper\(\)\)\.postDelayed.*?2200\)', new_logic.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/ui/SplashActivity.kt", "w") as f:
    f.write(content)
