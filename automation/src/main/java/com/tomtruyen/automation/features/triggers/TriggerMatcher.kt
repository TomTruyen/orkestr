package com.tomtruyen.automation.features.triggers

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate
import com.tomtruyen.automation.generated.GeneratedTriggerProvider

class TriggerMatcher(
    delegates: List<TriggerDelegate<out TriggerConfig>> = GeneratedTriggerProvider.delegates()
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
