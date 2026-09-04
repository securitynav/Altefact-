import re

with open('app/src/main/res/values/colors.xml', 'r') as f:
    content = f.read()

content = content.replace('</resources>', '    <color name="on_primary">#FFFFFF</color>\n    <color name="on_secondary">#000000</color>\n    <color name="google_btn_bg">#FFFFFF</color>\n    <color name="google_btn_text">#757575</color>\n    <color name="google_btn_stroke">#DADCE0</color>\n</resources>')

with open('app/src/main/res/values/colors.xml', 'w') as f:
    f.write(content)


with open('app/src/main/res/values-night/colors.xml', 'r') as f:
    content2 = f.read()

content2 = content2.replace('</resources>', '    <color name="on_primary">#000000</color>\n    <color name="on_secondary">#000000</color>\n    <color name="google_btn_bg">#16202D</color>\n    <color name="google_btn_text">#FFFFFF</color>\n    <color name="google_btn_stroke">#39FF14</color>\n</resources>')

with open('app/src/main/res/values-night/colors.xml', 'w') as f:
    f.write(content2)

