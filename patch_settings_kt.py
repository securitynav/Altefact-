import re

with open('app/src/main/java/com/securitynav/security/ui/SettingsActivity.kt', 'r') as f:
    content = f.read()

imports = """
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.securitynav.security.util.OtaUpdateManager
import android.widget.Toast
"""

content = content.replace('import com.securitynav.security.data.AuthManager', 'import com.securitynav.security.data.AuthManager' + imports)

ota_logic = """
        val btnCheckUpdates = findViewById<Button>(R.id.btnCheckUpdates)
        btnCheckUpdates.setOnClickListener {
            Toast.makeText(this, "Buscando actualizaciones en el servidor...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                try {
                    val otaManager = OtaUpdateManager(this@SettingsActivity)
                    val updateInfo = otaManager.checkForUpdates()
                    if (updateInfo.isUpdateAvailable) {
                        Toast.makeText(this@SettingsActivity, "Actualización: ${updateInfo.versionName}. Descargando...", Toast.LENGTH_LONG).show()
                        otaManager.downloadAndInstallApk(updateInfo.downloadUrl) { progress ->
                            // background download
                        }
                    } else {
                        Toast.makeText(this@SettingsActivity, "Estás en la última versión", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SettingsActivity, "Error conectando al servidor OTA", Toast.LENGTH_SHORT).show()
                }
            }
        }
"""

content = content.replace('val btnLogout = findViewById<Button>(R.id.btnLogout)', ota_logic + '\n        val btnLogout = findViewById<Button>(R.id.btnLogout)')

with open('app/src/main/java/com/securitynav/security/ui/SettingsActivity.kt', 'w') as f:
    f.write(content)
