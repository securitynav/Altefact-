import re

with open("app/src/main/res/menu/drawer_menu.xml", "r") as f:
    content = f.read()

profile_item = """
        <item
            android:id="@+id/nav_profile"
            android:icon="@android:drawable/ic_menu_myplaces"
            android:title="Mi Perfil" />
"""

content = content.replace(
    '<item android:title="">',
    profile_item + '\n    <item android:title="">'
)

with open("app/src/main/res/menu/drawer_menu.xml", "w") as f:
    f.write(content)

