with open('app/src/main/res/layout/activity_settings.xml', 'r') as f:
    content = f.read()

# I will add an OTA update button
button_xml = """
        <Button
            android:id="@+id/btnCheckUpdates"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Buscar Actualizaciones (OTA)"
            android:backgroundTint="@color/neon_cyan"
            android:textColor="@color/black"
            android:textStyle="bold"
            android:layout_marginTop="24dp" />
"""

content = content.replace('<Button\n            android:id="@+id/btnLogout"', button_xml + '\n        <Button\n            android:id="@+id/btnLogout"')

with open('app/src/main/res/layout/activity_settings.xml', 'w') as f:
    f.write(content)
