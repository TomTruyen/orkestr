package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(GpsStateConstraintConfig.DISCRIMINATOR)
data class GpsStateConstraintConfig(val enabled: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.GPS_STATE
    override val category: AutomationCategory = AutomationCategory.LOCATION

    companion object {
        const val DISCRIMINATOR = "gps_state_constraint"
    }
}
