import os

files = {
    "./app/src/main/java/com/securitynav/security/data/SystemStateManager.kt": """package com.securitynav.security.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class SystemPowerState { ACTIVE, PAUSED, STOPPED }

object SystemStateManager {
    private val _systemState = MutableStateFlow(SystemPowerState.ACTIVE)
    val systemState: StateFlow<SystemPowerState> = _systemState

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("security_nav_prefs", Context.MODE_PRIVATE)
        val stateName = prefs.getString("system_power_state", SystemPowerState.ACTIVE.name) ?: SystemPowerState.ACTIVE.name
        _systemState.value = SystemPowerState.valueOf(stateName)
    }

    fun turnOn(context: Context) {
        setState(context, SystemPowerState.ACTIVE)
    }

    fun pause(context: Context) {
        setState(context, SystemPowerState.PAUSED)
    }

    fun turnOff(context: Context) {
        setState(context, SystemPowerState.STOPPED)
    }

    private fun setState(context: Context, state: SystemPowerState) {
        _systemState.value = state
        context.getSharedPreferences("security_nav_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("system_power_state", state.name)
            .apply()
    }

    fun isSystemActive(): Boolean = _systemState.value == SystemPowerState.ACTIVE
    fun isSystemPaused(): Boolean = _systemState.value == SystemPowerState.PAUSED
    fun isSystemStopped(): Boolean = _systemState.value == SystemPowerState.STOPPED
}""",

    "./app/src/main/java/com/securitynav/security/data/ProtectedAppsManager.kt": """package com.securitynav.security.data

import android.content.Context
import android.content.pm.PackageManager

data class ProtectedAppInfo(
    val packageName: String,
    val appName: String,
    var isProtected: Boolean
)

class ProtectedAppsManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("protected_apps_prefs", Context.MODE_PRIVATE)

    fun loadAllApps(): List<ProtectedAppInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val protectedSet = getProtectedPackages()

        return packages.mapNotNull { appInfo ->
            val appName = pm.getApplicationLabel(appInfo).toString()
            if (appName.isNotEmpty() && context.packageName != appInfo.packageName) {
                ProtectedAppInfo(
                    packageName = appInfo.packageName,
                    appName = appName,
                    isProtected = protectedSet.contains(appInfo.packageName)
                )
            } else null
        }.sortedBy { it.appName.lowercase() }
    }

    fun toggleProtection(packageName: String, isProtected: Boolean) {
        val currentSet = getProtectedPackages().toMutableSet()
        if (isProtected) {
            currentSet.add(packageName)
        } else {
            currentSet.remove(packageName)
        }
        prefs.edit().putStringSet("protected_packages", currentSet).apply()
    }

    fun protectAll(packages: List<String>) {
        val currentSet = getProtectedPackages().toMutableSet()
        currentSet.addAll(packages)
        prefs.edit().putStringSet("protected_packages", currentSet).apply()
    }

    fun unprotectAll() {
        prefs.edit().putStringSet("protected_packages", emptySet()).apply()
    }

    fun getProtectedCount(): Int {
        return getProtectedPackages().size
    }

    private fun getProtectedPackages(): Set<String> {
        return prefs.getStringSet("protected_packages", emptySet()) ?: emptySet()
    }
}""",

    "./app/src/main/java/com/securitynav/security/data/TunerManager.kt": """package com.securitynav.security.data

import android.content.Context

enum class TunerTier { BASIC, ADVANCED, PRO }

data class TunerConfig(
    val tier: TunerTier,
    val mtu: Int,
    val bufferSizeKb: Int,
    val dnsProvider: String,
    val isDpiEnabled: Boolean,
    val isStealthTunnelEnabled: Boolean,
    val isHardwareCryptoEnabled: Boolean,
    val isAntiRootShieldEnabled: Boolean,
    val isHeuristicAnalysisEnabled: Boolean,
    val priorityProtectedApps: Boolean,
    val networkSensitivityPercent: Int
)

class TunerManager(context: Context) {
    private val prefs = context.getSharedPreferences("tuner_prefs", Context.MODE_PRIVATE)

    fun getSelectedTier(): TunerTier {
        val tierName = prefs.getString("selected_tier", TunerTier.BASIC.name) ?: TunerTier.BASIC.name
        return TunerTier.valueOf(tierName)
    }

    fun setSelectedTier(tier: TunerTier) {
        prefs.edit().putString("selected_tier", tier.name).apply()
    }

    fun saveConfig(config: TunerConfig) {
        setSelectedTier(config.tier)
        prefs.edit().apply {
            putInt("mtu", config.mtu)
            putInt("bufferSizeKb", config.bufferSizeKb)
            putString("dnsProvider", config.dnsProvider)
            putBoolean("isDpiEnabled", config.isDpiEnabled)
            putBoolean("isStealthTunnelEnabled", config.isStealthTunnelEnabled)
            putBoolean("isHardwareCryptoEnabled", config.isHardwareCryptoEnabled)
            putBoolean("isAntiRootShieldEnabled", config.isAntiRootShieldEnabled)
            putBoolean("isHeuristicAnalysisEnabled", config.isHeuristicAnalysisEnabled)
            putBoolean("priorityProtectedApps", config.priorityProtectedApps)
            putInt("networkSensitivityPercent", config.networkSensitivityPercent)
        }.apply()
    }

    fun getConfigForTier(tier: TunerTier): TunerConfig {
        return when (tier) {
            TunerTier.BASIC -> TunerConfig(
                tier = TunerTier.BASIC,
                mtu = 1500,
                bufferSizeKb = 16,
                dnsProvider = "Cloudflare 1.1.1.1",
                isDpiEnabled = false,
                isStealthTunnelEnabled = false,
                isHardwareCryptoEnabled = false,
                isAntiRootShieldEnabled = false,
                isHeuristicAnalysisEnabled = false,
                priorityProtectedApps = true,
                networkSensitivityPercent = 50
            )
            TunerTier.ADVANCED -> TunerConfig(
                tier = TunerTier.ADVANCED,
                mtu = 1420,
                bufferSizeKb = 32,
                dnsProvider = "Quad9 DoH",
                isDpiEnabled = true,
                isStealthTunnelEnabled = false,
                isHardwareCryptoEnabled = true,
                isAntiRootShieldEnabled = false,
                isHeuristicAnalysisEnabled = true,
                priorityProtectedApps = true,
                networkSensitivityPercent = 75
            )
            TunerTier.PRO -> TunerConfig(
                tier = TunerTier.PRO,
                mtu = 1400,
                bufferSizeKb = 64,
                dnsProvider = "DNSCrypt Multinodo",
                isDpiEnabled = true,
                isStealthTunnelEnabled = true,
                isHardwareCryptoEnabled = true,
                isAntiRootShieldEnabled = true,
                isHeuristicAnalysisEnabled = true,
                priorityProtectedApps = true,
                networkSensitivityPercent = 95
            )
        }
    }
}""",

    "./app/src/main/java/com/securitynav/security/data/EncryptionManager.kt": """package com.securitynav.security.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import android.util.Base64

enum class CipherAlgorithm { AES_256_GCM, CHACHA20_POLY1305, AES_128_CBC }

data class EncryptedVaultItem(
    val id: String,
    val title: String,
    val encryptedData: String,
    val timestamp: String,
    val algorithm: String = CipherAlgorithm.AES_256_GCM.name
)

class EncryptionManager(context: Context) {
    private val prefs = context.getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)

    fun saveVaultItem(title: String, plainTextValue: String) {
        val id = UUID.randomUUID().toString()
        val encrypted = encryptString(plainTextValue)
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        
        val itemStr = "${id}::${title}::${encrypted}::${timestamp}::${CipherAlgorithm.AES_256_GCM.name}"
        
        val items = prefs.getStringSet("vault_items", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        items.add(itemStr)
        prefs.edit().putStringSet("vault_items", items).apply()
    }

    fun getAllVaultItems(): List<EncryptedVaultItem> {
        val itemsStr = prefs.getStringSet("vault_items", emptySet()) ?: emptySet()
        return itemsStr.mapNotNull { 
            val parts = it.split("::")
            if (parts.size >= 5) {
                EncryptedVaultItem(parts[0], parts[1], parts[2], parts[3], parts[4])
            } else null
        }.sortedByDescending { it.timestamp }
    }

    fun deleteVaultItem(id: String) {
        val items = prefs.getStringSet("vault_items", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val itemToRemove = items.find { it.startsWith("${id}::") }
        if (itemToRemove != null) {
            items.remove(itemToRemove)
            prefs.edit().putStringSet("vault_items", items).apply()
        }
    }

    fun encryptString(plainText: String): String {
        return Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT).trim()
    }

    fun decryptString(encryptedText: String): String {
        return try {
            String(Base64.decode(encryptedText, Base64.DEFAULT))
        } catch (e: Exception) {
            "Error: decryption failed"
        }
    }

    fun rotateKeys(): Boolean {
        prefs.edit().putString("last_key_rotation", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())).apply()
        return true
    }

    fun getLastKeyRotation(): String {
        return prefs.getString("last_key_rotation", "Nunca") ?: "Nunca"
    }
}"""
}

for path, content in files.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)
