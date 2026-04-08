package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.PowerConnectedConstraintConfig

@GenerateConstraintDefinition
object PowerConnectedConstraintDefinition : ConstraintDefinition<PowerConnectedConstraintConfig>(
    configClass = PowerConnectedConstraintConfig::class,
    defaultConfig = PowerConnectedConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_power_connected_title
    override val descriptionRes = R.string.automation_definition_constraint_power_connected_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = PowerConnectedConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "connected",
            labelRes = R.string.automation_definition_constraint_power_connected_field_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_power_connected_field_description,
            defaultValue = true.toString(),
            reader = { it.connected.toString() },
            updater = { config, value -> config.copy(connected = value.toBoolean()) },
        ),
    )

    override fun summarize(config: PowerConnectedConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_power_connected_summary,
            listOf(resolver.resolve(config.connected.connectedDisconnectedRes())),
        )
}
