package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatterySaverStateChangedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.BatterySaverStateTriggerConfig

@GenerateTriggerDelegate
class BatterySaverStateTriggerDelegate : TriggerDelegate<BatterySaverStateTriggerConfig> {
    override val type: TriggerType = TriggerType.BATTERY_SAVER_STATE

    override fun matches(config: BatterySaverStateTriggerConfig, event: AutomationEvent): Boolean =
        event is BatterySaverStateChangedEvent && event.enabled == config.enabled
}
