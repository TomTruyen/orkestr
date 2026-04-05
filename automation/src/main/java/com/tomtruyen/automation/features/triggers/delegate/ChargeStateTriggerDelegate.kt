package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig

@GenerateTriggerDelegate
class ChargeStateTriggerDelegate: TriggerDelegate<BatteryChangedTriggerConfig> {
    override val type: TriggerType = TriggerType.CHARGE_STATE

    override fun matches(config: BatteryChangedTriggerConfig, event: AutomationEvent): Boolean {
        return event is BatteryChangedEvent && event.chargeState == config.state
    }
}
