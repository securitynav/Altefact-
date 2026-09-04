with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace(
    'testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"',
    'testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"\n        manifestPlaceholders["MAPS_API_KEY"] = "YOUR_MAPS_API_KEY_HERE"'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
