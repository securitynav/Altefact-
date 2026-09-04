import re

with open("app/src/main/java/com/securitynav/security/data/security/KeyStoreManager.kt", "r") as f:
    content = f.read()

content = content.replace(
    'import android.security.keystore.KeyProperties',
    'import android.security.keystore.KeyProperties\nimport android.security.keystore.StrongBoxUnavailableException'
)

# Add StrongBox backing try-catch
keygen = """
            val builder = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                
            try {
                keyGenerator.init(builder.setIsStrongBoxBacked(true).build())
                return keyGenerator.generateKey()
            } catch (e: StrongBoxUnavailableException) {
                // Fallback to normal hardware-backed keystore
                keyGenerator.init(builder.setIsStrongBoxBacked(false).build())
                return keyGenerator.generateKey()
            } catch (e: Exception) {
                keyGenerator.init(builder.setIsStrongBoxBacked(false).build())
                return keyGenerator.generateKey()
            }
"""

content = re.sub(
    r'val keyGenSpec = KeyGenParameterSpec\.Builder\(.*?\.build\(\)\s*keyGenerator\.init\(keyGenSpec\)\s*return keyGenerator\.generateKey\(\)',
    keygen.strip(),
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/securitynav/security/data/security/KeyStoreManager.kt", "w") as f:
    f.write(content)
