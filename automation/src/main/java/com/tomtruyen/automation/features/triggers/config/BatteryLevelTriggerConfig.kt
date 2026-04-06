package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BatteryLevelTriggerConfig.DISCRIMINATOR)
data class BatteryLevelTriggerConfig(
    val operator: ComparisonOperator = ComparisonOperator.LESS_THAN_OR_EQUAL,
    val value: Int = 20,
) : TriggerConfig {
    override val type: TriggerType = TriggerType.BATTERY_LEVEL
    override val category: AutomationCategory = AutomationCategory.BATTERY_POWER
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.BATTERY_CHANGED)

    companion object {
        const val DISCRIMINATOR = "battery_level_trigger"
    }
}
