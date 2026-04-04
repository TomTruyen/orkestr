package com.tomtruyen.automation.features.triggers

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.automation.features.triggers.delegate.ChargeStateTriggerDelegate
import com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate

class TriggerMatcher(
    delegates: List<TriggerDelegate<out TriggerConfig>> = listOf(
        ChargeStateTriggerDelegate()
    )
) {
    private val delegatesByType = delegates.associateBy { it.type }

    fun matches(triggers: List<TriggerConfig>, event: AutomationEvent): Boolean {
        return triggers.any { trigger ->
            delegatesByType[trigger.type]?.matchesTyped(trigger, event) ?: false
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun TriggerDelegate<out TriggerConfig>.matchesTyped(
    config: TriggerConfig,
    event: AutomationEvent
): Boolean = (this as TriggerDelegate<TriggerConfig>).matches(config, event)
