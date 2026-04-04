package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BatteryLevelConstraintConfig.DISCRIMINATOR)
data class BatteryLevelConstraintConfig(
    val operator: ComparisonOperator = ComparisonOperator.GREATER_THAN_OR_EQUAL,
    val value: Int = 80
) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.BATTERY_LEVEL

    companion object {
        const val DISCRIMINATOR = "battery_level"
    }
}
