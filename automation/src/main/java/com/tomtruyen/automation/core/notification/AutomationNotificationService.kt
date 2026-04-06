package com.tomtruyen.automation.core.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

class AutomationNotificationService(
    private val context: Context,
    private val factory: AutomationNotificationFactory = AutomationNotificationFactory(context),
    private val notificationManagerProvider: (Context) -> NotificationManager? = {
        it.getSystemService(NotificationManager::class.java)
    },
    private val notificationManagerCompatProvider: (Context) -> NotificationManagerCompat = {
        NotificationManagerCompat.from(it)
    },
) {
    fun ensureRuntimeChannel() {
        notificationManagerProvider(context)?.createNotificationChannel(factory.runtimeChannel())
    }

    fun buildRuntimeNotification() = factory.buildRuntimeNotification()

    fun ensureActionChannel() {
        notificationManagerProvider(context)?.createNotificationChannel(factory.actionChannel())
    }

    @SuppressLint("MissingPermission")
    fun showActionNotification(config: ShowNotificationActionConfig) {
        notificationManagerCompatProvider(context).notify(nextNotificationId(), factory.buildActionNotification(config))
    }

    private fun nextNotificationId(): Int = System.currentTimeMillis().toInt()
}
