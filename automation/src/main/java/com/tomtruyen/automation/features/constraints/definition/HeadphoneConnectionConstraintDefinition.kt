package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.HeadphoneConnectionConstraintConfig

@GenerateConstraintDefinition
object HeadphoneConnectionConstraintDefinition : ConstraintDefinition<HeadphoneConnectionConstraintConfig>(
    configClass = HeadphoneConnectionConstraintConfig::class,
    defaultConfig = HeadphoneConnectionConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_headphone_title
    override val descriptionRes = R.string.automation_definition_constraint_headphone_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = HeadphoneConnectionConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "connected",
            labelRes = R.string.automation_definition_constraint_headphone_field_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_headphone_field_description,
            defaultValue = true.toString(),
            reader = { it.connected.toString() },
            updater = { config, value -> config.copy(connected = value.toBoolean()) },
        ),
    )

    override fun summarize(config: HeadphoneConnectionConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_headphone_summary,
            listOf(resolver.resolve(config.connected.connectedDisconnectedRes())),
        )
}
