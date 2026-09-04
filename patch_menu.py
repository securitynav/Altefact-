with open('app/src/main/res/menu/drawer_menu.xml', 'r') as f:
    content = f.read()
    
content = content.replace('android:title="@string/nav_guard"', 'android:title="Escáner Antivirus"')

with open('app/src/main/res/menu/drawer_menu.xml', 'w') as f:
    f.write(content)
