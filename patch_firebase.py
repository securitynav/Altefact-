import re
import os

# Patch root build.gradle.kts
root_build = "build.gradle.kts"
if os.path.exists(root_build):
    with open(root_build, "r") as f:
        content = f.read()
    if "com.google.gms.google-services" not in content:
        content = content.replace(
            'id("com.android.application")',
            'id("com.google.gms.google-services") version "4.4.2" apply false\n    id("com.android.application")'
        )
        with open(root_build, "w") as f:
            f.write(content)

# Patch app build.gradle.kts
app_build = "app/build.gradle.kts"
if os.path.exists(app_build):
    with open(app_build, "r") as f:
        content = f.read()
    
    # Add plugin
    if "com.google.gms.google-services" not in content:
        content = content.replace(
            'id("com.android.application")',
            'id("com.android.application")\n    id("com.google.gms.google-services")'
        )
    
    # Add dependencies
    if "firebase-bom" not in content:
        content = content.replace(
            'dependencies {',
            'dependencies {\n    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))\n    implementation("com.google.firebase:firebase-database")\n    implementation("com.google.firebase:firebase-auth")'
        )
        
    # Apply code freeze / minify for release
    content = content.replace(
        'isMinifyEnabled = false',
        'isMinifyEnabled = true\n            isShrinkResources = true'
    )
        
    with open(app_build, "w") as f:
        f.write(content)

