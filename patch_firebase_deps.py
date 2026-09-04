import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

# Add firestore and storage if not there
if "firebase-firestore" not in content:
    content = content.replace(
        'implementation("com.google.firebase:firebase-auth")',
        'implementation("com.google.firebase:firebase-auth")\n    implementation("com.google.firebase:firebase-firestore")\n    implementation("com.google.firebase:firebase-storage")'
    )

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
