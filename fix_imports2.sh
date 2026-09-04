#!/bin/bash
# Remove all Intent and Uri imports, then add them exactly once at the top
sed -i '/import android.content.Intent/d' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt
sed -i '/import android.net.Uri/d' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt
sed -i '/package com.securitynav.security.ui/a \import android.content.Intent\nimport android.net.Uri' ./app/src/main/java/com/securitynav/security/ui/MainActivity.kt
