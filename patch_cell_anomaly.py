with open('app/src/main/java/com/securitynav/security/engine/CellAnomalyDetector.kt', 'w') as f:
    f.write("""package com.securitynav.security.engine

import android.content.Context
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.CellInfoNr
import android.telephony.TelephonyManager
import android.os.Build

data class RealCellData(
    val cellId: String,
    val type: String,
    val signalStrength: Int,
    val isRegistered: Boolean
)

class CellAnomalyDetector(private val context: Context) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @android.annotation.SuppressLint("MissingPermission")
    fun isDangerousDowngrade(): Boolean {
        return try {
            val networkType = telephonyManager.networkType
            // Detectar si la red cambia abruptamente a tecnologías sin cifrado fuerte (GSM/2G/GPRS/EDGE)
            when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_GSM -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun getRealCellData(): List<RealCellData> {
        val result = mutableListOf<RealCellData>()
        try {
            val allCellInfo = telephonyManager.allCellInfo ?: return result
            for (info in allCellInfo) {
                var cellId = "Unknown"
                var type = "Unknown"
                var signal = 0
                
                when (info) {
                    is CellInfoLte -> {
                        type = "LTE (4G)"
                        cellId = info.cellIdentity.ci.toString()
                        signal = info.cellSignalStrength.dbm
                    }
                    is CellInfoGsm -> {
                        type = "GSM (2G) - INSECURE!"
                        cellId = info.cellIdentity.cid.toString()
                        signal = info.cellSignalStrength.dbm
                    }
                    is CellInfoWcdma -> {
                        type = "WCDMA (3G)"
                        cellId = info.cellIdentity.cid.toString()
                        signal = info.cellSignalStrength.dbm
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && info is CellInfoNr) {
                    type = "NR (5G)"
                    val identity = info.cellIdentity as android.telephony.CellIdentityNr
                    cellId = identity.nci.toString()
                    signal = info.cellSignalStrength.dbm
                }
                
                if (cellId != "Unknown" && cellId != "2147483647") {
                    result.add(RealCellData(cellId, type, signal, info.isRegistered))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
""")
