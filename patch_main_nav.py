import re

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace(
    'R.id.nav_settings -> {',
    'R.id.nav_profile -> {\n                    startActivity(Intent(this, ProfileActivity::class.java))\n                    drawerLayout.closeDrawer(GravityCompat.START)\n                    return@setNavigationItemSelectedListener true\n                }\n                R.id.nav_settings -> {'
)

with open("app/src/main/java/com/securitynav/security/ui/MainActivity.kt", "w") as f:
    f.write(content)
