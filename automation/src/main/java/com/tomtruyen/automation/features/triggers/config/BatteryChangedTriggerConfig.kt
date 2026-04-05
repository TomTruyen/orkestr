package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BatteryChangedTriggerConfig.DISCRIMINATOR)
data class BatteryChangedTriggerConfig(val state: BatteryChargeState = BatteryChargeState.CHARGING) : TriggerConfig {
    override val type: TriggerType = TriggerType.CHARGE_STATE
    override val category: AutomationCategory = AutomationCategory.BATTERY_POWER
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.BATTERY_CHANGED)

    companion object {
        const val DISCRIMINATOR = "battery_changed"
    }
}
