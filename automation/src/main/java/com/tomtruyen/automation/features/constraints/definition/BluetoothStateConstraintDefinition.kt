package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.BluetoothStateConstraintConfig

@GenerateConstraintDefinition
object BluetoothStateConstraintDefinition : ConstraintDefinition<BluetoothStateConstraintConfig>(
    configClass = BluetoothStateConstraintConfig::class,
    defaultConfig = BluetoothStateConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_bluetooth_title
    override val descriptionRes = R.string.automation_definition_constraint_bluetooth_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = BluetoothStateConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "enabled",
            labelRes = R.string.automation_definition_constraint_bluetooth_field_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_bluetooth_field_description,
            defaultValue = true.toString(),
            reader = { it.enabled.toString() },
            updater = { config, value -> config.copy(enabled = value.toBoolean()) },
        ),
    )

    override fun summarize(config: BluetoothStateConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_bluetooth_summary,
            listOf(resolver.resolve(config.enabled.onOffRes())),
        )
}
