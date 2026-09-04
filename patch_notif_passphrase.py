import re

with open("app/src/main/java/com/securitynav/security/ui/NotificationsFragment.kt", "r") as f:
    content = f.read()

content = content.replace(
    'private val DB_PASSPHRASE = "secure_vault_key_123"',
    'private val DB_PASSPHRASE: String by lazy { com.securitynav.security.data.security.KeyStoreManager(requireContext()).getMasterPassphrase() }'
)

with open("app/src/main/java/com/securitynav/security/ui/NotificationsFragment.kt", "w") as f:
    f.write(content)

