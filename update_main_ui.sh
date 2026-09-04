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
        <item
            android:id="@+id/nav_notifications"
            android:icon="@android:drawable/ic_popup_reminder"
            android:title="@string/nav_notifications" />
    </group>
    
    <item android:title="">
        <menu>
            <item
                android:id="@+id/nav_settings"
                android:icon="@android:drawable/ic_menu_preferences"
                android:title="@string/advanced_configuration" />
            <item
                android:id="@+id/nav_support"
                android:icon="@android:drawable/ic_menu_send"
                android:title="@string/nav_support" />
        </menu>
    </item>
</menu>
MENU

sed -i 's/<TextView/<ImageView\n                android:layout_width="32dp"\n                android:layout_height="32dp"\n                android:src="@mipmap\/ic_launcher_round"\n                android:layout_marginEnd="12dp" \/>\n\n            <TextView/g' ./app/src/main/res/layout/activity_main.xml
