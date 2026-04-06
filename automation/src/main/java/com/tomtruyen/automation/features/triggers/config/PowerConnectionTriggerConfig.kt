package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.PowerConnectionState
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(PowerConnectionTriggerConfig.DISCRIMINATOR)
data class PowerConnectionTriggerConfig(val state: PowerConnectionState = PowerConnectionState.CONNECTED) :
    TriggerConfig {
    override val type: TriggerType = TriggerType.POWER_CONNECTION
    override val category: AutomationCategory = AutomationCategory.BATTERY_POWER
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.POWER_CONNECTION)

    companion object {
        const val DISCRIMINATOR = "power_connection"
    }
}
