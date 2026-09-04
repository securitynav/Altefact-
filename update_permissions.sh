#!/bin/bash
sed -i '/import android.os.Bundle/a \import android.Manifest\nimport android.content.pm.PackageManager\nimport android.os.Build\nimport android.provider.Settings\nimport androidx.core.app.ActivityCompat\nimport androidx.core.content.ContextCompat\nimport android.net.Uri' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt

sed -i '/com.securitynav.security.util.OtaUpdateManager(this).checkForUpdates()/i \
        requestPermissionsIfNeeded()' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt

sed -i '/private fun loadFragment(fragment: Fragment) {/i \
    private fun requestPermissionsIfNeeded() {\n        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {\n            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {\n                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)\n            }\n        }\n    }' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt
