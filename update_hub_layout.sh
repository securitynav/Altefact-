#!/bin/bash
cat << 'XML' > ./app/src/main/res/layout/fragment_security_hub.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg_light"
    android:orientation="vertical"
    android:padding="24dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center">

        <com.securitynav.security.ui.CustomPulseLockView
            android:id="@+id/btnMainLock"
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:layout_marginVertical="16dp" />

        <TextView
            android:id="@+id/tvStatus"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/sys_status_secure"
            android:textColor="@color/secondary"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginBottom="8dp" />

        <TextView
            android:id="@+id/tvBandwidth"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/bandwidth"
            android:textColor="@color/text_secondary"
            android:textSize="16sp" />
    </LinearLayout>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Real-Time Network Traffic"
        android:textColor="@color/primary"
        android:textSize="16sp"
        android:textStyle="bold"
        android:layout_marginTop="32dp"
        android:layout_marginBottom="16dp" />

    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        app:cardBackgroundColor="@color/surface_light"
        app:cardCornerRadius="12dp"
        app:strokeColor="@color/google_btn_stroke"
        app:strokeWidth="1dp"
        app:cardElevation="0dp">

        <com.github.mikephil.charting.charts.LineChart
            android:id="@+id/realTimeChart"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_margin="12dp" />
    </com.google.android.material.card.MaterialCardView>

</LinearLayout>
XML
echo "Layout updated"
