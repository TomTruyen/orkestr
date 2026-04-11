package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.NotificationPolicyAccessPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(DoNotDisturbModeTriggerConfig.DISCRIMINATOR)
data class DoNotDisturbModeTriggerConfig(val mode: DoNotDisturbMode = DoNotDisturbMode.OFF) : TriggerConfig {
    override val type: TriggerType = TriggerType.DO_NOT_DISTURB_MODE
    override val category: AutomationCategory = AutomationCategory.NOTIFICATIONS
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.DO_NOT_DISTURB_MODE)

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(NotificationPolicyAccessPermission)

    companion object {
        const val DISCRIMINATOR = "do_not_disturb_mode"
    }
}
