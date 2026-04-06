package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.TimeBasedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig

@GenerateTriggerDelegate
class TimeBasedTriggerDelegate : TriggerDelegate<TimeBasedTriggerConfig> {
    override val type: TriggerType = TriggerType.TIME_BASED

    override fun matches(config: TimeBasedTriggerConfig, event: AutomationEvent): Boolean = event is TimeBasedEvent &&
        event.hour == config.hour &&
        event.minute == config.minute &&
        event.day in config.days
}
