import re

with open('build.gradle.kts', 'r') as f:
    content = f.read()

content = content.replace('plugins {', 'plugins {\n    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false')

with open('build.gradle.kts', 'w') as f:
    f.write(content)

with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

content = content.replace('plugins {', 'plugins {\n    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")')
content = content.replace('android {', 'secrets {\n    propertiesFileName = ".env"\n    defaultPropertiesFileName = ".env.example"\n}\n\nandroid {')
content = content.replace('dependencies {', 'dependencies {\n    implementation("com.google.maps.android:maps-compose:4.3.3")\n    implementation("com.google.android.gms:play-services-maps:18.2.0")')

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
