package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(PowerConnectedConstraintConfig.DISCRIMINATOR)
data class PowerConnectedConstraintConfig(val connected: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.POWER_CONNECTED
    override val category: AutomationCategory = AutomationCategory.BATTERY_POWER

    companion object {
        const val DISCRIMINATOR = "power_connected_constraint"
    }
}
