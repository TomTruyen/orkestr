package com.tomtruyen.automation.features.triggers

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

interface TriggerDelegate<T: TriggerConfig> {
    fun matches(config: T, event: AutomationEvent): Boolean
}