#!/bin/bash
sed -i '/R.id.nav_settings -> {/i \                R.id.nav_support -> {\n                    val intent = Intent(Intent.ACTION_SENDTO).apply {\n                        data = android.net.Uri.parse("mailto:soporte@securitynav.local")\n                        putExtra(Intent.EXTRA_SUBJECT, "Support Request")\n                    }\n                    startActivity(Intent.createChooser(intent, "Contact Support"))\n                }' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt

sed -i '/R.id.nav_vault -> VaultFragment()/a \                        R.id.nav_notifications -> NotificationsFragment()' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt
