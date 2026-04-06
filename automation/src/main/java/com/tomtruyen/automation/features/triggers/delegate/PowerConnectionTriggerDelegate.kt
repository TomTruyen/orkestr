package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.PowerConnectionEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.PowerConnectionTriggerConfig

@GenerateTriggerDelegate
class PowerConnectionTriggerDelegate : TriggerDelegate<PowerConnectionTriggerConfig> {
    override val type: TriggerType = TriggerType.POWER_CONNECTION

    override fun matches(config: PowerConnectionTriggerConfig, event: AutomationEvent): Boolean =
        event is PowerConnectionEvent && event.state == config.state
}
