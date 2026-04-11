package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(HeadphoneConnectionTriggerConfig.DISCRIMINATOR)
data class HeadphoneConnectionTriggerConfig(val connected: Boolean = true) : TriggerConfig {
    override val type: TriggerType = TriggerType.HEADPHONE_CONNECTION
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.HEADPHONE_CONNECTION)

    companion object {
        const val DISCRIMINATOR = "headphone_connection"
    }
}
