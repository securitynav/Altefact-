with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace('propertiesFileName = ".env"', 'propertiesFileName = "../.env"')
content = content.replace('defaultPropertiesFileName = ".env.example"', 'defaultPropertiesFileName = "../.env.example"')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
