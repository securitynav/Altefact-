#!/bin/bash
cat << 'XML' > ./app/src/main/res/layout/activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg_light">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <!-- Header -->
        <androidx.constraintlayout.widget.ConstraintLayout
            android:id="@+id/topBar"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@color/surface_light"
            app:layout_constraintTop_toTopOf="parent">
            
            <ImageButton
                android:id="@+id/btnMenu"
                android:layout_width="40dp"
                android:layout_height="40dp"
                android:layout_marginStart="16dp"
                android:background="?attr/selectableItemBackgroundBorderless"
                android:src="@android:drawable/ic_menu_sort_by_size"
                app:tint="@color/primary"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintBottom_toBottomOf="parent" />

            <LinearLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintBottom_toBottomOf="parent">
                
                <ImageView
                    android:layout_width="32dp"
                    android:layout_height="32dp"
                    android:src="@mipmap/ic_launcher_round"
                    android:layout_marginEnd="12dp" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/app_name"
                    android:textColor="@color/primary"
                    android:textSize="20sp"
                    android:textStyle="bold" />
            </LinearLayout>
        </androidx.constraintlayout.widget.ConstraintLayout>

        <androidx.fragment.app.FragmentContainerView
            android:id="@+id/nav_host_fragment"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintTop_toBottomOf="@id/topBar"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@color/bg_light"
        app:itemTextColor="@color/text_primary"
        app:itemIconTint="@color/primary"
        app:menu="@menu/drawer_menu" />

</androidx.drawerlayout.widget.DrawerLayout>
XML
