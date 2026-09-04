#!/bin/bash
sed -i '/<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" \/>/a \    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />\n    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />' ./app/src/main/AndroidManifest.xml
