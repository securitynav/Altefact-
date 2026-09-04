import re

with open('app/src/main/res/values/colors.xml', 'r') as f:
    content = f.read()

content = content.replace('</resources>', '    <color name="bg_light">@color/background</color>\n    <color name="surface_light">@color/surface</color>\n</resources>')

with open('app/src/main/res/values/colors.xml', 'w') as f:
    f.write(content)


with open('app/src/main/res/values-night/colors.xml', 'r') as f:
    content2 = f.read()

content2 = content2.replace('</resources>', '    <color name="bg_light">@color/background</color>\n    <color name="surface_light">@color/surface</color>\n</resources>')

with open('app/src/main/res/values-night/colors.xml', 'w') as f:
    f.write(content2)

