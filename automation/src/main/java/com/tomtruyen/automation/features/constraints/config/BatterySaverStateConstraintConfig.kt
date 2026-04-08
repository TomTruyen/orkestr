package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BatterySaverStateConstraintConfig.DISCRIMINATOR)
data class BatterySaverStateConstraintConfig(val enabled: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.BATTERY_SAVER_STATE
    override val category: AutomationCategory = AutomationCategory.BATTERY_POWER

    companion object {
        const val DISCRIMINATOR = "battery_saver_state_constraint"
    }
}
