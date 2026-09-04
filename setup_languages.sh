#!/bin/bash

# 1. Create base English strings
cat << 'STR' > ./app/src/main/res/values/strings.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">SecurityNav</string>
    <string name="main_title">SECURITY NAV</string>

    <!-- Register Activity -->
    <string name="create_account">Create Account</string>
    <string name="full_name">Full Name</string>
    <string name="email_address">Email Address</string>
    <string name="secure_password">Secure Password</string>
    <string name="promo_code">Promo / Referral Code</string>
    <string name="apply">Apply</string>
    <string name="sign_up">Sign Up</string>
    <string name="or">or</string>
    <string name="sign_in_google">Sign in with Google</string>

    <string name="promo_verifying">Verifying...</string>
    <string name="promo_success">Success: %1$s (-%2$s%%)</string>
    <string name="promo_invalid">Invalid Code: %1$s</string>
    <string name="promo_server_error">Server Error: Could not validate code</string>
    <string name="promo_mock_success">Promo applied successfully! (Mock)</string>

    <!-- Settings Activity -->
    <string name="basic_configuration">Basic Configuration</string>
    <string name="realtime_notifications">Real-time Security Notifications</string>
    <string name="app_theme">App Theme</string>
    <string name="app_language">App Language</string>
    <string name="background_scan_frequency">Background Scan Frequency</string>
    <string name="advanced_configuration">Advanced Configuration</string>
    <string name="rotate_keys">Rotate SQLCipher Keys</string>
    <string name="vpn_exclusions">VPN App Exclusions</string>
    <string name="packet_inspection_verbosity">Packet Inspection Verbosity</string>
    <string name="anti_tampering">Anti-tampering / Root Check</string>
    <string name="sign_out">Sign Out</string>

    <string-array name="frequency_options">
        <item>Every 15 mins</item>
        <item>Hourly</item>
        <item>Daily</item>
    </string-array>
    
    <string-array name="verbosity_options">
        <item>Low</item>
        <item>Medium</item>
        <item>High (Full Packet)</item>
    </string-array>
    
    <string-array name="theme_options">
        <item>System Default</item>
        <item>Light Theme</item>
        <item>Dark Theme</item>
    </string-array>

    <string-array name="language_options">
        <item>System Default</item>
        <item>English</item>
        <item>Español</item>
    </string-array>

    <!-- Navigation / Dashboard -->
    <string name="nav_hub">Hub</string>
    <string name="nav_analytics">Analytics</string>
    <string name="nav_vpn">VPN</string>
    <string name="nav_guard">Guard</string>
    <string name="nav_vault">Vault</string>

    <string name="sys_status_secure">System Status: SECURE</string>
    <string name="bandwidth">Bandwidth: --</string>
    <string name="vpn_private_local">Private Local VPN</string>
    <string name="vpn_status_disconnected">VPN Status: DISCONNECTED</string>
    <string name="connect_vpn">Connect VPN</string>

    <string name="accessibility_guard">Accessibility Guard</string>
    <string name="accessibility_guard_desc">Monitors active screen overlays and window changes to prevent unauthorized access and tampering.</string>
    <string name="enable_in_settings">Enable in Settings</string>

    <string name="key_vault">Key Vault (SQLCipher)</string>
    <string name="key_vault_desc">Highly encrypted storage for API keys and sensitive security events using SQLCipher 256-bit AES.</string>
    <string name="unlock_vault">Unlock Vault</string>
    
    <string name="vuln_dashboard">Vulnerability Trends</string>
    <string name="vuln_dashboard_desc">Visualizing common vulnerability occurrences over time.</string>

    <string name="tuner_network">NETWORK &amp; BANDWIDTH TUNER</string>
</resources>
STR

# 2. Create Spanish strings
cat << 'STR' > ./app/src/main/res/values-es/strings.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">SecurityNav</string>
    <string name="main_title">SECURITY NAV</string>

    <!-- Register Activity -->
    <string name="create_account">Crear Cuenta</string>
    <string name="full_name">Nombre Completo</string>
    <string name="email_address">Correo Electrónico</string>
    <string name="secure_password">Contraseña Segura</string>
    <string name="promo_code">Código de Promoción / Referido</string>
    <string name="apply">Aplicar</string>
    <string name="sign_up">Registrarse</string>
    <string name="or">o</string>
    <string name="sign_in_google">Continuar con Google</string>

    <string name="promo_verifying">Verificando...</string>
    <string name="promo_success">Éxito: %1$s (-%2$s%%)</string>
    <string name="promo_invalid">Código Inválido: %1$s</string>
    <string name="promo_server_error">Error de servidor: No se pudo validar el código</string>
    <string name="promo_mock_success">¡Promoción aplicada con éxito! (Simulado)</string>

    <!-- Settings Activity -->
    <string name="basic_configuration">Configuración Básica</string>
    <string name="realtime_notifications">Notificaciones de Seguridad en Tiempo Real</string>
    <string name="app_theme">Tema de la App</string>
    <string name="app_language">Idioma de la App</string>
    <string name="background_scan_frequency">Frecuencia de Escaneo en Segundo Plano</string>
    <string name="advanced_configuration">Configuración Avanzada</string>
    <string name="rotate_keys">Rotar Llaves de SQLCipher</string>
    <string name="vpn_exclusions">Exclusiones de VPN (Apps)</string>
    <string name="packet_inspection_verbosity">Nivel de Inspección de Paquetes</string>
    <string name="anti_tampering">Antimanipulación / Comprobación Root</string>
    <string name="sign_out">Cerrar Sesión</string>

    <string-array name="frequency_options">
        <item>Cada 15 min</item>
        <item>Cada hora</item>
        <item>Diariamente</item>
    </string-array>
    
    <string-array name="verbosity_options">
        <item>Bajo</item>
        <item>Medio</item>
        <item>Alto (Paquete completo)</item>
    </string-array>
    
    <string-array name="theme_options">
        <item>Predeterminado del sistema</item>
        <item>Modo Claro</item>
        <item>Modo Oscuro</item>
    </string-array>

    <string-array name="language_options">
        <item>Predeterminado del sistema</item>
        <item>English</item>
        <item>Español</item>
    </string-array>

    <!-- Navigation / Dashboard -->
    <string name="nav_hub">Centro</string>
    <string name="nav_analytics">Analítica</string>
    <string name="nav_vpn">VPN</string>
    <string name="nav_guard">Guardia</string>
    <string name="nav_vault">Bóveda</string>

    <string name="sys_status_secure">Estado de Sistema: SEGURO</string>
    <string name="bandwidth">Ancho de banda: --</string>
    <string name="vpn_private_local">VPN Local Privada</string>
    <string name="vpn_status_disconnected">Estado VPN: DESCONECTADO</string>
    <string name="connect_vpn">Conectar VPN</string>

    <string name="accessibility_guard">Guardia de Accesibilidad</string>
    <string name="accessibility_guard_desc">Monitorea ventanas emergentes y cambios de pantalla para evitar accesos no autorizados y manipulación.</string>
    <string name="enable_in_settings">Habilitar en Configuración</string>

    <string name="key_vault">Bóveda de Llaves (SQLCipher)</string>
    <string name="key_vault_desc">Almacenamiento altamente cifrado para llaves API y eventos de seguridad sensibles usando SQLCipher AES de 256 bits.</string>
    <string name="unlock_vault">Desbloquear Bóveda</string>
    
    <string name="vuln_dashboard">Tendencias de Vulnerabilidad</string>
    <string name="vuln_dashboard_desc">Visualización de ocurrencias de vulnerabilidades comunes a lo largo del tiempo.</string>

    <string name="tuner_network">SINTONIZADOR DE RED Y ANCHO DE BANDA</string>
</resources>
STR

echo "Strings configured successfully."
