import os

files = {
    "./app/src/main/res/drawable/bg_badge_secure.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="12dp" />
    <solid android:color="@color/secondary" />
</shape>""",

    "./app/src/main/res/layout/item_protected_app.xml": """<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    app:cardBackgroundColor="@color/surface_light"
    app:cardCornerRadius="12dp"
    app:strokeColor="@color/google_btn_stroke"
    app:strokeWidth="1dp"
    app:cardElevation="0dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp"
        android:gravity="center_vertical">

        <ImageView
            android:id="@+id/ivAppIcon"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:layout_marginEnd="12dp" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/tvAppName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="@color/text_primary"
                android:textSize="15sp"
                android:textStyle="bold" />

            <TextView
                android:id="@+id/tvAppPackage"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="@color/text_secondary"
                android:textSize="11sp" />

            <TextView
                android:id="@+id/tvBadge"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp"
                android:background="@drawable/bg_badge_secure"
                android:paddingHorizontal="6dp"
                android:paddingVertical="2dp"
                android:text="BLINDADA"
                android:textColor="@color/white"
                android:textSize="9sp"
                android:textStyle="bold" />
        </LinearLayout>

        <com.google.android.material.materialswitch.MaterialSwitch
            android:id="@+id/switchProtection"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>""",

    "./app/src/main/res/layout/fragment_protected_apps.xml": """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg_light"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/protected_apps_warehouse"
        android:textColor="@color/text_primary"
        android:textSize="20sp"
        android:textStyle="bold" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="2dp"
        android:layout_marginBottom="16dp"
        android:text="@string/protected_apps_desc"
        android:textColor="@color/text_secondary"
        android:textSize="13sp" />

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="12dp"
        app:boxBackgroundMode="outline"
        app:boxCornerRadiusBottomEnd="12dp"
        app:boxCornerRadiusBottomStart="12dp"
        app:boxCornerRadiusTopEnd="12dp"
        app:boxCornerRadiusTopStart="12dp"
        app:hintEnabled="false">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etSearchApp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:drawableStart="@android:drawable/ic_menu_search"
            android:drawablePadding="8dp"
            android:hint="@string/search_apps_hint"
            android:inputType="text"
            android:textSize="14sp" />
    </com.google.android.material.textfield.TextInputLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="12dp"
        android:orientation="horizontal">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnProtectAll"
            style="?attr/materialButtonOutlinedStyle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginEnd="8dp"
            android:layout_weight="1"
            android:text="@string/protect_all"
            android:textColor="@color/secondary"
            app:strokeColor="@color/secondary" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnUnprotectAll"
            style="?attr/materialButtonOutlinedStyle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="@string/unprotect_all"
            android:textColor="@color/text_secondary"
            app:strokeColor="@color/text_secondary" />
    </LinearLayout>

    <TextView
        android:id="@+id/tvProtectedSummary"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="12dp"
        android:textColor="@color/text_primary"
        android:textSize="13sp"
        android:textStyle="bold" />

    <ProgressBar
        android:id="@+id/pbLoadingApps"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="32dp" />

    <TextView
        android:id="@+id/tvEmptyApps"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="32dp"
        android:text="@string/no_apps_found"
        android:textColor="@color/text_secondary"
        android:visibility="gone" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvProtectedApps"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingBottom="16dp" />
</LinearLayout>"""
}

for path, content in files.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)
