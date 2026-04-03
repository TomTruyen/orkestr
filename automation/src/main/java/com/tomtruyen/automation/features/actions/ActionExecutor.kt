package com.tomtruyen.automation.features.actions

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.config.ActionConfig

class ActionExecutor(
    private val delegates: Map<ActionType, ActionDelegate<ActionConfig>>
) {
    suspend fun executeAll(actions: List<ActionConfig>, event: AutomationEvent) {
        actions.forEach {
            delegates[it.type]?.execute(it, event)
        }
    }
}