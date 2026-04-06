package com.tomtruyen.automation.features.triggers.receiver

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.NotificationReceivedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AutomationNotificationListenerService :
    NotificationListenerService(),
    KoinComponent {
    private val runtimeService by inject<AutomationRuntimeService>()
    private val logger by inject<AutomationLogger>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) {
            logger.log("Ignoring self-notification from ${sbn.packageName}")
            return
        }

        val extras = sbn.notification.extras
        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val message = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        logger.log("Received notification from ${sbn.packageName}")
        scope.launch {
            runtimeService.handleEvent(
                NotificationReceivedEvent(
                    packageName = sbn.packageName,
                    title = title,
                    message = message,
                ),
            )
        }
    }
}
