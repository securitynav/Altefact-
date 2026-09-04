import re

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "r") as f:
    content = f.read()

imports = """
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.securitynav.security.billing.SubscriptionManager
import com.securitynav.security.util.OtaUpdateManager
import android.widget.Toast
"""

content = content.replace("import android.os.Bundle", imports + "import android.os.Bundle")

init_code = """
        // Initialize Billing
        SubscriptionManager.initGooglePlayBilling(this) {
            // Check if we need to do anything on success
        }

        // Check for OTA Updates
        lifecycleScope.launch {
            val otaManager = OtaUpdateManager(this@MainActivity)
            val updateInfo = otaManager.checkForUpdates()
            if (updateInfo.isUpdateAvailable) {
                Toast.makeText(this@MainActivity, "Actualización disponible: ${updateInfo.versionName}. Descargando...", Toast.LENGTH_LONG).show()
                otaManager.downloadAndInstallApk(updateInfo.downloadUrl) { progress ->
                    // Could update a progress bar here
                }
            }
        }
        
        // Load default fragment
"""

content = content.replace("// Load default fragment", init_code)

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "w") as f:
    f.write(content)
