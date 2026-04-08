package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.ReadPhoneStatePermission
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(MobileDataStateConstraintConfig.DISCRIMINATOR)
data class MobileDataStateConstraintConfig(val enabled: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.MOBILE_DATA_STATE
    override val category: AutomationCategory = AutomationCategory.UTILITY

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(ReadPhoneStatePermission)

    companion object {
        const val DISCRIMINATOR = "mobile_data_state_constraint"
    }
}
