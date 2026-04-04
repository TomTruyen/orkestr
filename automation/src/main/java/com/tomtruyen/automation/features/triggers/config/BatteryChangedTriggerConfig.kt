package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.features.triggers.TriggerType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BatteryChangedTriggerConfig.DISCRIMINATOR)
data class BatteryChangedTriggerConfig(
    val state: BatteryChargeState = BatteryChargeState.CHARGING
) : TriggerConfig {
    override val type: TriggerType = TriggerType.CHARGE_STATE

    companion object {
        const val DISCRIMINATOR = "battery_changed"
    }
}
