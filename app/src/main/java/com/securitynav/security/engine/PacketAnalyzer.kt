package com.securitynav.security.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RealTrafficLog(
    val isOutbound: Boolean,
    val protocol: String,
    val port: Int,
    val sourceIp: String,
    val destinationIp: String,
    val payloadSize: Int,
    val timestamp: Long
)

object PacketAnalyzer {
    private val _recentPackets = MutableStateFlow<List<RealTrafficLog>>(emptyList())
    val recentPackets: StateFlow<List<RealTrafficLog>> = _recentPackets

    fun addPacket(packet: RealTrafficLog) {
        val current = _recentPackets.value.toMutableList()
        current.add(0, packet)
        if (current.size > 50) {
            current.removeLast()
        }
        _recentPackets.value = current
    }
}
