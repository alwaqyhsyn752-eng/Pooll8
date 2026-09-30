package com.example.aimassist.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.aimassist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "pooll8_overlay_channel"
        const val NOTIF_ID = 1001
    }

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                mgr.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Pooll8 Overlay", NotificationManager.IMPORTANCE_LOW)
                )
            }
        }
    }

    fun buildNotification(): Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Pooll8 Active")
            .setContentText("Overlay is running")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
}
