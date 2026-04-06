package com.tomtruyen.automation.core.notification

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

class AutomationNotificationFactory(private val context: Context) {
    fun runtimeChannel(): NotificationChannel = NotificationChannel(
        RUNTIME_CHANNEL_ID,
        "Automation runtime",
        android.app.NotificationManager.IMPORTANCE_LOW,
    ).apply {
        description = "Keeps Orkestr listening for automation triggers."
    }

    fun actionChannel(): NotificationChannel = NotificationChannel(
        ACTION_CHANNEL_ID,
        "Automation actions",
        android.app.NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = "Notifications posted by automation actions."
    }

    fun buildRuntimeNotification(): Notification {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        return NotificationCompat.Builder(context, RUNTIME_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("Automation service running")
            .setContentText("Listening for time, battery, and geofence events")
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(contentIntent)
            .build()
    }

    fun buildActionNotification(config: ShowNotificationActionConfig): Notification =
        NotificationCompat.Builder(context, ACTION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(config.title)
            .setContentText(config.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(config.message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

    companion object {
        const val RUNTIME_CHANNEL_ID = "automation_runtime"
        const val ACTION_CHANNEL_ID = "automation_actions"
    }
}
