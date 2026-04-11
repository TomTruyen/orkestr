package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.HeadphoneConnectionEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.HeadphoneConnectionTriggerConfig

@GenerateTriggerDelegate
class HeadphoneConnectionTriggerDelegate : TriggerDelegate<HeadphoneConnectionTriggerConfig> {
    override val type: TriggerType = TriggerType.HEADPHONE_CONNECTION

    override fun matches(config: HeadphoneConnectionTriggerConfig, event: AutomationEvent): Boolean =
        event is HeadphoneConnectionEvent && event.connected == config.connected
}
