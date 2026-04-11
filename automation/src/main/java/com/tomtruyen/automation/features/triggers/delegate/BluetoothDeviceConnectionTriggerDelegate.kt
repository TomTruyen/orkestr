package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BluetoothDeviceConnectionEvent
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.BluetoothDeviceConnectionTriggerConfig

@GenerateTriggerDelegate
class BluetoothDeviceConnectionTriggerDelegate : TriggerDelegate<BluetoothDeviceConnectionTriggerConfig> {
    override val type: TriggerType = TriggerType.BLUETOOTH_DEVICE_CONNECTION

    override fun matches(config: BluetoothDeviceConnectionTriggerConfig, event: AutomationEvent): Boolean =
        event is BluetoothDeviceConnectionEvent && event.connected == config.connected
}
