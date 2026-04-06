package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.ApplicationLifecycleEvent
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig

@GenerateTriggerDelegate
class ApplicationLifecycleTriggerDelegate : TriggerDelegate<ApplicationLifecycleTriggerConfig> {
    override val type: TriggerType = TriggerType.APPLICATION_LIFECYCLE

    override fun matches(config: ApplicationLifecycleTriggerConfig, event: AutomationEvent): Boolean =
        event is ApplicationLifecycleEvent &&
            event.packageName == config.packageName &&
            event.transitionType == config.transitionType
}
