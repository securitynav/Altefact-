import re

with open('app/src/main/java/com/securitynav/security/util/OtaUpdateManager.kt', 'r') as f:
    content = f.read()

content = content.replace('import kotlinx.coroutines.withContext', 'import kotlinx.coroutines.withContext\nimport kotlinx.coroutines.delay')
content = content.replace('val connection = (url.openConnection()', 'delay(1500) // Simular latencia de red\n            val connection = (url.openConnection()')

with open('app/src/main/java/com/securitynav/security/util/OtaUpdateManager.kt', 'w') as f:
    f.write(content)
