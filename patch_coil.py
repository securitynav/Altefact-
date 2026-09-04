import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace(
    'implementation("androidx.compose.material3:material3")',
    'implementation("androidx.compose.material3:material3")\n    implementation("io.coil-kt:coil-compose:2.6.0")\n    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(content)

