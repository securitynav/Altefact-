import re

with open("app/src/main/java/com/securitynav/security/ui/AuthActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import com.securitynav.security.ui.theme.SecurityNavTheme", "import com.securitynav.security.ui.theme.SecurityNavTheme\nimport com.securitynav.security.util.NetworkUtils")

net_logic = """
                    if (!NetworkUtils.isInternetAvailable(context)) {
                        Toast.makeText(context, "Sin conexión a Internet", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (email.isEmpty() || password.isEmpty()) {
"""

content = content.replace("if (email.isEmpty() || password.isEmpty()) {", net_logic.strip())

with open("app/src/main/java/com/securitynav/security/ui/AuthActivity.kt", "w") as f:
    f.write(content)
