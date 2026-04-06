package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.BatteryLevelTriggerConfig

@GenerateTriggerDelegate
class BatteryLevelTriggerDelegate : TriggerDelegate<BatteryLevelTriggerConfig> {
    override val type: TriggerType = TriggerType.BATTERY_LEVEL

    override fun matches(config: BatteryLevelTriggerConfig, event: AutomationEvent): Boolean =
        event is BatteryChangedEvent && config.operator.matches(event.percentage, config.value)
}
