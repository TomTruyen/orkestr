package com.tomtruyen.automation.features.actions.delegate

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.DoNotDisturbActionConfig
import com.tomtruyen.automation.core.model.DoNotDisturbMode

@GenerateActionDelegate
class DoNotDisturbActionDelegate(
    private val context: Context
) : ActionDelegate<DoNotDisturbActionConfig> {
    override val type: ActionType = ActionType.DO_NOT_DISTURB

    override suspend fun execute(config: DoNotDisturbActionConfig, event: AutomationEvent) {
        if (!config.requiredPermissions.all { it.isGranted(context) }) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val notificationManager = context.getSystemService(NotificationManager::class.java) ?: return
        if (!notificationManager.isNotificationPolicyAccessGranted) return

        notificationManager.setInterruptionFilter(
            when (config.mode) {
                DoNotDisturbMode.PRIORITY_ONLY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                DoNotDisturbMode.ALARMS_ONLY -> NotificationManager.INTERRUPTION_FILTER_ALARMS
                DoNotDisturbMode.TOTAL_SILENCE -> NotificationManager.INTERRUPTION_FILTER_NONE
                DoNotDisturbMode.OFF -> NotificationManager.INTERRUPTION_FILTER_ALL
            }
        )
    }
}
