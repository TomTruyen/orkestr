package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.BluetoothConnectPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(BluetoothDeviceConnectionTriggerConfig.DISCRIMINATOR)
data class BluetoothDeviceConnectionTriggerConfig(val connected: Boolean = true) : TriggerConfig {
    override val type: TriggerType = TriggerType.BLUETOOTH_DEVICE_CONNECTION
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.BLUETOOTH_DEVICE_CONNECTION)

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(BluetoothConnectPermission)

    companion object {
        const val DISCRIMINATOR = "bluetooth_device_connection"
    }
}
