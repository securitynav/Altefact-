import re

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "r") as f:
    content = f.read()

nav_logic = """
                R.id.nav_logout -> {
                    // Limpiar llaves en RAM (simulado cerrando la Activity)
                    val intent = Intent(this, PinActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
"""

content = re.sub(r'R\.id\.nav_logout -> \{.*?\n\s*\}', nav_logic.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "w") as f:
    f.write(content)
