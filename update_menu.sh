#!/bin/bash
cat << 'MENU' > ./app/src/main/res/menu/drawer_menu.xml
<?xml version="1.0" encoding="utf-8"?>
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
            android:id="@+id/nav_guard"
            android:icon="@android:drawable/ic_menu_view"
            android:title="@string/nav_guard" />
        <item
            android:id="@+id/nav_vault"
            android:icon="@android:drawable/ic_menu_manage"
            android:title="@string/nav_vault" />
    </group>
    
    <item android:title="">
        <menu>
            <item
                android:id="@+id/nav_settings"
                android:icon="@android:drawable/ic_menu_preferences"
                android:title="@string/advanced_configuration" />
        </menu>
    </item>
</menu>
MENU

echo "Menu created"
