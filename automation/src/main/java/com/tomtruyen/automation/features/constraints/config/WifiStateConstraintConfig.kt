package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(WifiStateConstraintConfig.DISCRIMINATOR)
data class WifiStateConstraintConfig(val enabled: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.WIFI_STATE
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "wifi_state_constraint"
    }
}
