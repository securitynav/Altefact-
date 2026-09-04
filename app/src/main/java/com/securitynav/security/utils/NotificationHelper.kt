package com.securitynav.security.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.securitynav.security.R

object NotificationHelper {
    private const val CHANNEL_ID = "rogue_tower_alerts"
    
    fun showRogueTowerAlert(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rogue Tower Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for persistent rogue cell tower connections"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("¡Alerta de Seguridad Crítica!")
            .setContentText("Detectada exposición prolongada (>5 min) a un IMSI Catcher cercano.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
