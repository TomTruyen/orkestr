package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(ScreenStateConstraintConfig.DISCRIMINATOR)
data class ScreenStateConstraintConfig(val on: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.SCREEN_STATE
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "screen_state_constraint"
    }
}
