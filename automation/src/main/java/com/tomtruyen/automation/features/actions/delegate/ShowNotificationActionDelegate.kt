package com.tomtruyen.automation.features.actions.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

class ShowNotificationActionDelegate: ActionDelegate<ShowNotificationActionConfig> {
    override val type: ActionType = ActionType.SHOW_NOTIFICATION

    override suspend fun execute(config: ShowNotificationActionConfig, event: AutomationEvent) {
        // TODO: Show notification
        println("This should be come a notification " + config.message)
    }
}