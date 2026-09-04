import re

with open("app/src/main/res/menu/drawer_menu.xml", "r") as f:
    content = f.read()

logout_item = """
        <item
            android:id="@+id/nav_logout"
            android:icon="@android:drawable/ic_lock_power_off"
            android:title="Cerrar Sesión" />
"""
if "nav_logout" not in content:
    content = content.replace(
        '</menu>',
        logout_item + '\n</menu>'
    )
    with open("app/src/main/res/menu/drawer_menu.xml", "w") as f:
        f.write(content)

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "r") as f:
    content = f.read()

imports = """
import com.google.firebase.auth.FirebaseAuth
import com.securitynav.security.crypto.KeyStoreManager
"""
if "KeyStoreManager" not in content:
    content = content.replace("import com.securitynav.security.billing.SubscriptionManager", imports + "\nimport com.securitynav.security.billing.SubscriptionManager")

init_code = """
        // Initialize Hardware Key
        try {
            KeyStoreManager(this).getMasterPassphrase()
        } catch(e: Exception) {
            // Ignored, just to initialize
        }
"""
if "KeyStoreManager(this)" not in content:
    content = content.replace(
        'SubscriptionManager.initGooglePlayBilling(this) {',
        init_code + '\n        SubscriptionManager.initGooglePlayBilling(this) {'
    )

nav_logic = """
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    // Remove keys or data here if needed
                    val intent = Intent(this, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
"""
if "nav_logout" not in content:
    content = content.replace(
        'R.id.nav_settings -> {',
        nav_logic.strip() + '\n                R.id.nav_settings -> {'
    )

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "w") as f:
    f.write(content)

