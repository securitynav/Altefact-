package com.securitynav.security.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LeakEvent(
    val id: String,
    val vector: LeakVector,
    val source: String,
    val clearPayload: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val blurredPayload: String
        get() = "••••••••••••••••"
}

enum class LeakVector(val label: String, val colorHex: String) {
    APP("Vía Apps", "#00F0FF"),
    ROGUE_TOWER("Vía Torres Falsas", "#BF00FF"),
    NETWORK_DNS("Vía Red/DNS", "#FF0055"),
    MALWARE("Vía Virus", "#FFB300")
}

object DataLeakDetector {
    private val _leaks = MutableStateFlow<List<LeakEvent>>(emptyList())
    val leaks: StateFlow<List<LeakEvent>> = _leaks



    fun triggerSimulatedLeak(vector: LeakVector, source: String, payload: String) {
        val currentList = _leaks.value.toMutableList()
        currentList.add(0, LeakEvent(System.currentTimeMillis().toString(), vector, source, payload))
        _leaks.value = currentList
    }
}
