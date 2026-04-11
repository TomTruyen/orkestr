package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.triggers.config.BluetoothDeviceConnectionTriggerConfig

@GenerateTriggerDefinition
object BluetoothDeviceConnectionTriggerDefinition : TriggerDefinition<BluetoothDeviceConnectionTriggerConfig>(
    configClass = BluetoothDeviceConnectionTriggerConfig::class,
    defaultConfig = BluetoothDeviceConnectionTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_bluetooth_device_title
    override val descriptionRes = R.string.automation_definition_trigger_bluetooth_device_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = BluetoothDeviceConnectionTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_CONNECTED,
            labelRes = R.string.automation_definition_trigger_connection_state_field_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_bluetooth_device_field_description,
            defaultValue = VALUE_CONNECTED,
            options = connectionOptions(),
            reader = { it.connected.toConnectionFieldValue() },
            updater = { config, value -> config.copy(connected = value.toConnected()) },
        ),
    )

    override fun summarize(config: BluetoothDeviceConnectionTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_bluetooth_device_summary,
            listOf(resolver.resolve(config.connected.toConnectionLabelRes())),
        )
}
