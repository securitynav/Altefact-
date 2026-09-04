#!/bin/bash

# activity_register.xml
sed -i 's/android:text="Create Account"/android:text="@string\/create_account"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:hint="Full Name"/android:hint="@string\/full_name"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:hint="Email Address"/android:hint="@string\/email_address"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:hint="Secure Password"/android:hint="@string\/secure_password"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:hint="Promo \/ Referral Code"/android:hint="@string\/promo_code"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:text="Apply"/android:text="@string\/apply"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:text="Sign Up"/android:text="@string\/sign_up"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:text="or"/android:text="@string\/or"/g' ./app/src/main/res/layout/activity_register.xml
sed -i 's/android:text="Sign in with Google"/android:text="@string\/sign_in_google"/g' ./app/src/main/res/layout/activity_register.xml

# activity_settings.xml
sed -i 's/android:text="Basic Configuration"/android:text="@string\/basic_configuration"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Real-time Security Notifications"/android:text="@string\/realtime_notifications"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="App Theme"/android:text="@string\/app_theme"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Background Scan Frequency"/android:text="@string\/background_scan_frequency"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Advanced Configuration"/android:text="@string\/advanced_configuration"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Rotate SQLCipher Keys"/android:text="@string\/rotate_keys"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="VPN App Exclusions"/android:text="@string\/vpn_exclusions"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Packet Inspection Verbosity"/android:text="@string\/packet_inspection_verbosity"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Anti-tampering \/ Root Check"/android:text="@string\/anti_tampering"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Sign Out"/android:text="@string\/sign_out"/g' ./app/src/main/res/layout/activity_settings.xml
sed -i 's/android:text="Cerrar Sesión \/ Logout"/android:text="@string\/sign_out"/g' ./app/src/main/res/layout/activity_settings.xml

# activity_main.xml & activity_splash.xml
sed -i 's/android:text="SecurityNav"/android:text="@string\/app_name"/g' ./app/src/main/res/layout/activity_main.xml
sed -i 's/android:text="SecurityNav"/android:text="@string\/app_name"/g' ./app/src/main/res/layout/activity_splash.xml

# bottom_nav_menu.xml
sed -i 's/android:title="Hub"/android:title="@string\/nav_hub"/g' ./app/src/main/res/menu/bottom_nav_menu.xml
sed -i 's/android:title="Analytics"/android:title="@string\/nav_analytics"/g' ./app/src/main/res/menu/bottom_nav_menu.xml
sed -i 's/android:title="VPN"/android:title="@string\/nav_vpn"/g' ./app/src/main/res/menu/bottom_nav_menu.xml
sed -i 's/android:title="Guard"/android:title="@string\/nav_guard"/g' ./app/src/main/res/menu/bottom_nav_menu.xml
sed -i 's/android:title="Vault"/android:title="@string\/nav_vault"/g' ./app/src/main/res/menu/bottom_nav_menu.xml

# custom fragments
sed -i 's/android:text="Accessibility Guard"/android:text="@string\/accessibility_guard"/g' ./app/src/main/res/layout/fragment_guard.xml
sed -i 's/android:text="Monitors active screen overlays and window changes to prevent unauthorized access and tampering."/android:text="@string\/accessibility_guard_desc"/g' ./app/src/main/res/layout/fragment_guard.xml
sed -i 's/android:text="Enable in Settings"/android:text="@string\/enable_in_settings"/g' ./app/src/main/res/layout/fragment_guard.xml

sed -i 's/android:text="System Status: SECURE"/android:text="@string\/sys_status_secure"/g' ./app/src/main/res/layout/fragment_security_hub.xml
sed -i 's/android:text="Bandwidth: --"/android:text="@string\/bandwidth"/g' ./app/src/main/res/layout/fragment_security_hub.xml

sed -i 's/android:text="Key Vault (SQLCipher)"/android:text="@string\/key_vault"/g' ./app/src/main/res/layout/fragment_vault.xml
sed -i 's/android:text="Highly encrypted storage for API keys and sensitive security events using SQLCipher 256-bit AES."/android:text="@string\/key_vault_desc"/g' ./app/src/main/res/layout/fragment_vault.xml
sed -i 's/android:text="Unlock Vault"/android:text="@string\/unlock_vault"/g' ./app/src/main/res/layout/fragment_vault.xml

sed -i 's/android:text="Private Local VPN"/android:text="@string\/vpn_private_local"/g' ./app/src/main/res/layout/fragment_vpn.xml
sed -i 's/android:text="VPN Status: DISCONNECTED"/android:text="@string\/vpn_status_disconnected"/g' ./app/src/main/res/layout/fragment_vpn.xml
sed -i 's/android:text="Connect VPN"/android:text="@string\/connect_vpn"/g' ./app/src/main/res/layout/fragment_vpn.xml

sed -i 's/android:text="Vulnerability Trends"/android:text="@string\/vuln_dashboard"/g' ./app/src/main/res/layout/fragment_vulnerability_dashboard.xml
sed -i 's/android:text="Visualizing common vulnerability occurrences over time."/android:text="@string\/vuln_dashboard_desc"/g' ./app/src/main/res/layout/fragment_vulnerability_dashboard.xml

sed -i 's/android:text="TUNER DE RED &amp; ANCHO DE BANDA"/android:text="@string\/tuner_network"/g' ./app/src/main/res/layout/custom_tuner_container.xml

echo "Layouts updated successfully."
