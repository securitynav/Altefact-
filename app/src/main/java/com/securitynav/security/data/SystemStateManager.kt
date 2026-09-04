package com.securitynav.security.data

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
}