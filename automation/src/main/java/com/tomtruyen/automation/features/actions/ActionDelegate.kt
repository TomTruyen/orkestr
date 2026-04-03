package com.tomtruyen.automation.features.actions

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.config.ActionConfig

interface ActionDelegate<T: ActionConfig> {
    suspend fun execute(config: T, event: AutomationEvent)
}