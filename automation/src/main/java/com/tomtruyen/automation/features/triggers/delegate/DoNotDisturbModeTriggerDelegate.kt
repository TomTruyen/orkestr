package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.DoNotDisturbModeChangedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.DoNotDisturbModeTriggerConfig

@GenerateTriggerDelegate
class DoNotDisturbModeTriggerDelegate : TriggerDelegate<DoNotDisturbModeTriggerConfig> {
    override val type: TriggerType = TriggerType.DO_NOT_DISTURB_MODE

    override fun matches(config: DoNotDisturbModeTriggerConfig, event: AutomationEvent): Boolean =
        event is DoNotDisturbModeChangedEvent && event.mode == config.mode
}
