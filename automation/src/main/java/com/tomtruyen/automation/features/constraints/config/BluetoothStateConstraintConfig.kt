package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.BluetoothConnectPermission
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(BluetoothStateConstraintConfig.DISCRIMINATOR)
data class BluetoothStateConstraintConfig(val enabled: Boolean = true) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.BLUETOOTH_STATE
    override val category: AutomationCategory = AutomationCategory.UTILITY

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(BluetoothConnectPermission)

    companion object {
        const val DISCRIMINATOR = "bluetooth_state_constraint"
    }
}
