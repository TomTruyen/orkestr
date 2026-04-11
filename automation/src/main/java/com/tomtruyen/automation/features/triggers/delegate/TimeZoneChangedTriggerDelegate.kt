package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.TimeZoneChangedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TimeZoneChangedTriggerConfig

@GenerateTriggerDelegate
class TimeZoneChangedTriggerDelegate : TriggerDelegate<TimeZoneChangedTriggerConfig> {
    override val type: TriggerType = TriggerType.TIME_ZONE_CHANGED

    override fun matches(config: TimeZoneChangedTriggerConfig, event: AutomationEvent): Boolean =
        event is TimeZoneChangedEvent
}
