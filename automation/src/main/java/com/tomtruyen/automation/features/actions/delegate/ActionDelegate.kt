package com.tomtruyen.automation.features.actions.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig

interface ActionDelegate<T : ActionConfig> {
    val type: ActionType
    suspend fun execute(config: T, event: AutomationEvent)
}
