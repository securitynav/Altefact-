with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace(
    'implementation("androidx.compose.material3:material3")',
    'implementation("androidx.compose.material3:material3")\n    implementation("androidx.compose.material:material-icons-extended")'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
