package com.tomtruyen.automation.features.triggers

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

class TriggerMatcher(
    private val delegates: Map<TriggerType, TriggerDelegate<TriggerConfig>>
) {
    fun matches(triggers: List<TriggerConfig>, event: AutomationEvent): Boolean {
        return triggers.any { trigger ->
            delegates[trigger.type]?.matches(trigger, event) ?: false
        }
    }
}