package com.tomtruyen.automation.features.actions.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.notification.AutomationNotificationService
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

@GenerateActionDelegate
class ShowNotificationActionDelegate(private val context: Context) : ActionDelegate<ShowNotificationActionConfig> {
    override val type: ActionType = ActionType.SHOW_NOTIFICATION
    private val notificationService = AutomationNotificationService(context)

    override suspend fun execute(config: ShowNotificationActionConfig, event: AutomationEvent) {
        if (!hasRequiredPermissions(config)) {
            return
        }

        notificationService.ensureActionChannel()
        notificationService.showActionNotification(config)
    }

    private fun hasRequiredPermissions(config: ShowNotificationActionConfig): Boolean =
        config.requiredPermissions.all { permission ->
            permission.isGranted(context)
        }
}
