with open('app/src/main/res/menu/drawer_menu.xml', 'r') as f:
    content = f.read()

# The content has two nav_logout items:
# one inside a <menu> inside an <item>, and one outside.
# Let's cleanly rewrite the drawer_menu.xml
new_xml = """<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <group android:checkableBehavior="single">
        <item
            android:id="@+id/nav_hub"
            android:icon="@android:drawable/ic_menu_compass"
            android:title="@string/nav_hub" />
        <item
            android:id="@+id/nav_analytics"
            android:icon="@android:drawable/ic_menu_sort_by_size"
            android:title="@string/nav_analytics" />
        <item
            android:id="@+id/nav_vpn"
            android:icon="@android:drawable/ic_lock_lock"
            android:title="@string/nav_vpn" />
        <item
            android:id="@+id/nav_antennas"
            android:icon="@android:drawable/ic_menu_mylocation"
            android:title="Detector de Antenas" />
        <item
            android:id="@+id/nav_guard"
            android:icon="@android:drawable/ic_menu_view"
            android:title="Escáner Antivirus" />
        <item
            android:id="@+id/nav_vault"
            android:icon="@android:drawable/ic_menu_manage"
            android:title="@string/nav_vault" />
    </group>
    
    <item
        android:id="@+id/nav_profile"
        android:icon="@android:drawable/ic_menu_myplaces"
        android:title="Mi Perfil" />
        
    <item android:title="">
        <menu>
            <item
                android:id="@+id/nav_settings"
                android:icon="@android:drawable/ic_menu_preferences"
                android:title="@string/advanced_configuration" />
            <item
                android:id="@+id/nav_logout"
                android:icon="@android:drawable/ic_lock_power_off"
                android:title="Cerrar Sesión" />
        </menu>
    </item>
</menu>
"""

with open('app/src/main/res/menu/drawer_menu.xml', 'w') as f:
    f.write(new_xml)
