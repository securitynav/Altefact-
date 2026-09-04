import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

# Add buildConfigField
content = content.replace(
    'versionName = "1.0"',
    'versionName = "1.0"\n        buildConfigField("String", "RENDER_SERVER_URL", "\\"https://securitynav-service.onrender.com/api/v1/\\"")'
)

# Add billing dependency
if 'billing-ktx' not in content:
    content = content.replace(
        'implementation("com.google.android.gms:play-services-auth:20.7.0")',
        'implementation("com.google.android.gms:play-services-auth:20.7.0")\n    implementation("com.android.billingclient:billing-ktx:6.2.1")'
    )

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
