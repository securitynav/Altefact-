import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

content = content.replace(
    '<activity android:name=".ui.AuthActivity" android:exported="false" />',
    '<activity android:name=".ui.AuthActivity" android:exported="false" />\n        <activity android:name=".ui.PinActivity" android:exported="false" />'
)

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
