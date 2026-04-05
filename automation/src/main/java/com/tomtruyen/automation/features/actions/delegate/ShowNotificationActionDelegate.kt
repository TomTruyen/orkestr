package com.tomtruyen.automation.features.actions.delegate

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

@GenerateActionDelegate
class ShowNotificationActionDelegate(
    private val context: Context
) : ActionDelegate<ShowNotificationActionConfig> {
    override val type: ActionType = ActionType.SHOW_NOTIFICATION

    override suspend fun execute(config: ShowNotificationActionConfig, event: AutomationEvent) {
        if (!hasRequiredPermissions(config)) {
            return
        }

        createNotificationChannelIfNeeded()

        // TODO: Set an Action to perform after click (like opening an app)
        // TODO: Replace the SmallIcon with the actual icon of the app
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(config.title)
            .setContentText(config.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(config.message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        @SuppressLint("MissingPermission") // Checked by hasRequiredPermissions
        NotificationManagerCompat.from(context).notify(nextNotificationId(), notification)
    }

    private fun hasRequiredPermissions(config: ShowNotificationActionConfig): Boolean {
        return config.requiredPermissions.all { permission -> permission.isGranted(context) }
    }

    private fun createNotificationChannelIfNeeded() {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = NOTIFICATION_CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun nextNotificationId(): Int = System.currentTimeMillis().toInt()

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "automation_actions"
        private const val NOTIFICATION_CHANNEL_NAME = "Automation actions"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications posted by automation actions."
    }
}
