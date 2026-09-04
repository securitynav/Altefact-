import os

os.makedirs('app/src/main/res/values-night', exist_ok=True)

with open('app/src/main/res/values/colors.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- LIGHT MODE COLORS -->
    <color name="primary">#1A73E8</color> <!-- Google Blue -->
    <color name="secondary">#00C4B4</color>
    <color name="background">#F8F9FA</color>
    <color name="surface">#FFFFFF</color>
    
    <color name="text_primary">#202124</color>
    <color name="text_secondary">#5F6368</color>
    
    <color name="error">#D93025</color>
    <color name="success">#34A853</color>
    <color name="warning">#F9AB00</color>
    
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    
    <!-- We keep these for compatibility if some XML layout still references them directly, but point them to the light theme counterparts -->
    <color name="bg_dark">@color/background</color>
    <color name="surface_dark">@color/surface</color>
    <color name="neon_cyan">@color/primary</color>
    <color name="neon_purple">@color/secondary</color>
    <color name="neon_green">@color/success</color>
    <color name="neon_red">@color/error</color>
    <color name="neon_yellow">@color/warning</color>
</resources>
""")

with open('app/src/main/res/values-night/colors.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- DARK MODE COLORS (Cyberpunk / Hacker OLED) -->
    <color name="primary">#00F0FF</color>
    <color name="secondary">#B026FF</color>
    <color name="background">#0A131F</color>
    <color name="surface">#16202D</color>
    
    <color name="text_primary">#FFFFFF</color>
    <color name="text_secondary">#8B9BB4</color>
    
    <color name="error">#FF0055</color>
    <color name="success">#39FF14</color>
    <color name="warning">#FFD700</color>
</resources>
""")

with open('app/src/main/res/values/themes.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.SecurityNav" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorSecondary">@color/secondary</item>
        <item name="android:colorBackground">@color/background</item>
        <item name="colorSurface">@color/surface</item>
        <item name="colorOnSurface">@color/text_primary</item>
        <item name="colorError">@color/error</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>
</resources>
""")

