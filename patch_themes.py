with open('app/src/main/res/values/themes.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.SecurityNav" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorOnPrimary">@color/black</item>
        <item name="colorSecondary">@color/secondary</item>
        <item name="android:colorBackground">@color/bg_dark</item>
        <item name="colorSurface">@color/surface_dark</item>
        <item name="colorOnSurface">@color/text_primary</item>
        <item name="android:textColor">@color/text_primary</item>
        <item name="materialCardViewStyle">@style/Widget.SecurityNav.CardView</item>
        <item name="materialButtonStyle">@style/Widget.SecurityNav.Button</item>
    </style>
    <style name="Widget.SecurityNav.CardView" parent="Widget.Material3.CardView.Elevated">
        <item name="cardBackgroundColor">@color/surface_dark</item>
        <item name="strokeColor">@color/neon_cyan</item>
        <item name="strokeWidth">1dp</item>
        <item name="cardCornerRadius">12dp</item>
        <item name="cardElevation">4dp</item>
    </style>
    <style name="Widget.SecurityNav.Button" parent="Widget.Material3.Button">
        <item name="backgroundTint">@color/primary</item>
        <item name="android:textColor">@color/black</item>
        <item name="android:textStyle">bold</item>
        <item name="cornerRadius">8dp</item>
    </style>
</resources>
""")
