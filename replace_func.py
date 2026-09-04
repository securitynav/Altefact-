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
