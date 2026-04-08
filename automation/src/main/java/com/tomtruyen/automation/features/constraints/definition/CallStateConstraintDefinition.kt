package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.CallStateConstraintConfig

@GenerateConstraintDefinition
object CallStateConstraintDefinition : ConstraintDefinition<CallStateConstraintConfig>(
    configClass = CallStateConstraintConfig::class,
    defaultConfig = CallStateConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_call_state_title
    override val descriptionRes = R.string.automation_definition_constraint_call_state_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = CallStateConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "inCall",
            labelRes = R.string.automation_definition_constraint_call_state_field_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_call_state_field_description,
            defaultValue = true.toString(),
            reader = { it.inCall.toString() },
            updater = { config, value -> config.copy(inCall = value.toBoolean()) },
        ),
    )

    override fun summarize(config: CallStateConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_call_state_summary,
            listOf(resolver.resolve(config.inCall.inCallIdleRes())),
        )
}
