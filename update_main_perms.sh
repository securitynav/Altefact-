#!/bin/bash
sed -i '/import android.Manifest/a \import android.content.Intent\nimport android.net.Uri\nimport android.os.PowerManager' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt

cat << 'KOTLIN' > add_battery_perm.txt
    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
KOTLIN

# We will use python to replace the function since sed over multiple lines is tricky
cat << 'PYTHON' > replace_func.py
import re

with open('./app/src/main/java/com/securitynav/security/ui/MainActivity.kt', 'r') as file:
    content = file.read()

with open('add_battery_perm.txt', 'r') as file:
    new_func = file.read()

# Replace existing requestPermissionsIfNeeded
pattern = r'private fun requestPermissionsIfNeeded\(\) \{[\s\S]*?\}\n    \}'
content = re.sub(pattern, new_func, content)

with open('./app/src/main/java/com/securitynav/security/ui/MainActivity.kt', 'w') as file:
    file.write(content)
PYTHON
python3 replace_func.py
