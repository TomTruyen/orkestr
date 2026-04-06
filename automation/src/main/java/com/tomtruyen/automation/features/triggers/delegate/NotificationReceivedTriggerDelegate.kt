package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.NotificationReceivedEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig

@GenerateTriggerDelegate
class NotificationReceivedTriggerDelegate : TriggerDelegate<NotificationReceivedTriggerConfig> {
    override val type: TriggerType = TriggerType.NOTIFICATION_RECEIVED

    override fun matches(config: NotificationReceivedTriggerConfig, event: AutomationEvent): Boolean =
        event is NotificationReceivedEvent && event.packageName == config.packageName
}
