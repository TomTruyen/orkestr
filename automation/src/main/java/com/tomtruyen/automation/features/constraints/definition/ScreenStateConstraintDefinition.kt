package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.ScreenStateConstraintConfig

@GenerateConstraintDefinition
object ScreenStateConstraintDefinition : ConstraintDefinition<ScreenStateConstraintConfig>(
    configClass = ScreenStateConstraintConfig::class,
    defaultConfig = ScreenStateConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_screen_state_title
    override val descriptionRes = R.string.automation_definition_constraint_screen_state_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = ScreenStateConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "on",
            labelRes = R.string.automation_definition_constraint_screen_state_field_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_screen_state_field_description,
            defaultValue = true.toString(),
            reader = { it.on.toString() },
            updater = { config, value -> config.copy(on = value.toBoolean()) },
        ),
    )

    override fun summarize(config: ScreenStateConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_screen_state_summary,
            listOf(resolver.resolve(config.on.onOffRes())),
        )
}
