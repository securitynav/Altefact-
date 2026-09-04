package com.securitynav.security.data

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
}