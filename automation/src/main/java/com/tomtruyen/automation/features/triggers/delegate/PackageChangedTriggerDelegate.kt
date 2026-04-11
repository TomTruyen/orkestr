package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.PackageChangedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.PackageChangedTriggerConfig

@GenerateTriggerDelegate
class PackageChangedTriggerDelegate : TriggerDelegate<PackageChangedTriggerConfig> {
    override val type: TriggerType = TriggerType.PACKAGE_CHANGED

    override fun matches(config: PackageChangedTriggerConfig, event: AutomationEvent): Boolean =
        event is PackageChangedEvent &&
            event.changeType == config.changeType &&
            config.packageName.trim().let { it.isBlank() || it == event.packageName }
}
