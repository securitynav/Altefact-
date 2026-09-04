import re

with open('app/src/main/java/com/securitynav/security/ui/SettingsActivity.kt', 'r') as f:
    content = f.read()

imports = """
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
"""

content = content.replace('import android.widget.Toast', 'import android.widget.Toast' + imports)

theme_logic = """
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchTheme)
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        switchTheme.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES || currentMode == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
        
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
"""

content = content.replace('val freqSpinner = findViewById<Spinner>(R.id.spinnerFrequency)', theme_logic + '\n        val freqSpinner = findViewById<Spinner>(R.id.spinnerFrequency)')

with open('app/src/main/java/com/securitynav/security/ui/SettingsActivity.kt', 'w') as f:
    f.write(content)
