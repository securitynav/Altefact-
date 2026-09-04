import re
with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace('implementation("com.google.maps.android:maps-compose:4.3.3")', 
                          'implementation("com.google.maps.android:maps-compose:4.3.3")\n    implementation("com.google.maps.android:maps-compose-utils:4.3.3")\n    implementation("com.google.maps.android:android-maps-utils:3.8.0")')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
