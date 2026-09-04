import re

missing_strings = [
    "background_scan_frequency", "basic_configuration", "connect_vpn", "create_account",
    "email_address", "enable_in_settings", "encryption_status_desc", "full_name",
    "key_vault", "key_vault_desc", "loading_apps", "or", "packet_inspection_verbosity",
    "perm_background", "power_pause", "power_turn_off", "power_turn_on", "promo_code",
    "promo_invalid", "promo_mock_success", "promo_server_error", "promo_success",
    "promo_verifying", "realtime_notifications", "rotate_keys", "secure_password",
    "sign_in_google", "sign_out", "sign_up", "sys_sub_secure", "tuner_network",
    "tuner_tier_basic", "unlock_vault", "vpn_exclusions", "vpn_private_local",
    "vpn_status_disconnected", "vuln_dashboard", "vuln_dashboard_desc",
    "accessibility_guard", "accessibility_guard_desc", "alert_penetration",
    "alert_data_leak", "anti_tampering", "app_theme", "app_language"
]

xml_additions = ""
for s in missing_strings:
    xml_additions += f'    <string name="{s}">{s.replace("_", " ").title()}</string>\n'

xml_additions += '''
    <string-array name="frequency_options">
        <item>1 hour</item>
        <item>24 hours</item>
    </string-array>
    <string-array name="language_options">
        <item>English</item>
        <item>Spanish</item>
    </string-array>
    <string-array name="theme_options">
        <item>Light</item>
        <item>Dark</item>
    </string-array>
    <string-array name="verbosity_options">
        <item>Low</item>
        <item>High</item>
    </string-array>
'''

with open('./app/src/main/res/values/strings.xml', 'r') as f:
    content = f.read()

content = content.replace("</resources>", xml_additions + "</resources>")

with open('./app/src/main/res/values/strings.xml', 'w') as f:
    f.write(content)
