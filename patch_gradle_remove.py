import re

with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

content = re.sub(r'implementation\("com.github.PhilJay:MPAndroidChart:v3.1.0"\)', '', content)

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
