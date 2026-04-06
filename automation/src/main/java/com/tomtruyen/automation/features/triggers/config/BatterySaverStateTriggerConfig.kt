package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BatterySaverStateTriggerConfig.DISCRIMINATOR)
data class BatterySaverStateTriggerConfig(val enabled: Boolean = true) : TriggerConfig {
    override val type: TriggerType = TriggerType.BATTERY_SAVER_STATE
    override val category: AutomationCategory = AutomationCategory.BATTERY_POWER
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.BATTERY_SAVER)

    companion object {
        const val DISCRIMINATOR = "battery_saver_state"
    }
}
