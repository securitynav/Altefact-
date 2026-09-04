package com.securitynav.security.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProtectedApp(
    val id: Int, 
    val name: String, 
    val packageName: String,
    val iconType: String, 
    var isProtected: Boolean,
    var isCritical: Boolean = false,
    var isTerminated: Boolean = false
)

object VaultManager {
    private val _apps = MutableStateFlow(listOf(
        ProtectedApp(1, "WhatsApp", "com.whatsapp", "message", true, false),
        ProtectedApp(2, "Telegram", "org.telegram.messenger", "message", true, false),
        ProtectedApp(3, "Banca Móvil", "com.bank.app", "bank", true, false),
        ProtectedApp(4, "Galería Fotos", "com.android.gallery3d", "photo", false, false),
        ProtectedApp(5, "Correo Electrónico", "com.google.android.gm", "email", false, false)
    ))
    val apps: StateFlow<List<ProtectedApp>> = _apps

    fun toggleProtection(id: Int, isProtected: Boolean) {
        _apps.value = _apps.value.map { if (it.id == id) it.copy(isProtected = isProtected) else it }
    }

    fun toggleCritical(id: Int, isCritical: Boolean) {
        _apps.value = _apps.value.map { if (it.id == id) it.copy(isCritical = isCritical) else it }
    }
    
    fun terminateApp(packageName: String) {
        _apps.value = _apps.value.map { if (it.packageName == packageName) it.copy(isTerminated = true) else it }
    }
}
