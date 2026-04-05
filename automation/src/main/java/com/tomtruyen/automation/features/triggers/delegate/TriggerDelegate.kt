package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

interface TriggerDelegate<T : TriggerConfig> {
    val type: TriggerType
    fun matches(config: T, event: AutomationEvent): Boolean
}
