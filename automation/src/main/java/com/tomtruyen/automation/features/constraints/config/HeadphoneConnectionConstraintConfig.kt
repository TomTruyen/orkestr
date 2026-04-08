package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(HeadphoneConnectionConstraintConfig.DISCRIMINATOR)
data class HeadphoneConnectionConstraintConfig(val connected: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.HEADPHONE_CONNECTION
    override val category: AutomationCategory = AutomationCategory.VOLUME

    companion object {
        const val DISCRIMINATOR = "headphone_connection_constraint"
    }
}
