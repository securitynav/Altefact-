package com.securitynav.security.data

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
}