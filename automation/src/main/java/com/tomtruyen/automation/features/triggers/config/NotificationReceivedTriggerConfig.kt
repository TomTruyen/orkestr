package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.NotificationListenerPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(NotificationReceivedTriggerConfig.DISCRIMINATOR)
data class NotificationReceivedTriggerConfig(val packageName: String = "") : TriggerConfig {
    override val type: TriggerType = TriggerType.NOTIFICATION_RECEIVED
    override val category: AutomationCategory = AutomationCategory.NOTIFICATIONS

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(NotificationListenerPermission)

    companion object {
        const val DISCRIMINATOR = "notification_received"
    }
}
