package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.AutomationLocalTime
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig

@GenerateConstraintDefinition
object TimeOfDayConstraintDefinition : ConstraintDefinition<TimeOfDayConstraintConfig>(
    configClass = TimeOfDayConstraintConfig::class,
    defaultConfig = TimeOfDayConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_time_of_day_title
    override val descriptionRes = R.string.automation_definition_constraint_time_of_day_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = TimeOfDayConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "startTime",
            labelRes = R.string.automation_definition_constraint_time_of_day_start_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_constraint_time_of_day_start_description,
            defaultValue = defaultConfig.startTime.format24Hour(),
            placeholderRes = R.string.automation_definition_constraint_time_of_day_placeholder,
            reader = { it.startTime.format24Hour() },
            updater = { config, value ->
                AutomationLocalTime.parse(value)?.let { parsed -> config.copy(startTime = parsed) } ?: config
            },
            inputValidator = ::validateTime,
        ),
        TypedAutomationFieldDefinition(
            configClass = TimeOfDayConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "endTime",
            labelRes = R.string.automation_definition_constraint_time_of_day_end_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_constraint_time_of_day_end_description,
            defaultValue = defaultConfig.endTime.format24Hour(),
            placeholderRes = R.string.automation_definition_constraint_time_of_day_placeholder,
            reader = { it.endTime.format24Hour() },
            updater = { config, value ->
                AutomationLocalTime.parse(value)?.let { parsed -> config.copy(endTime = parsed) } ?: config
            },
            inputValidator = ::validateTime,
        ),
    )

    override fun summarize(config: TimeOfDayConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_time_of_day_summary,
            listOf(config.startTime.format24Hour(), config.endTime.format24Hour()),
        )

    private fun validateTime(input: String, resolver: AutomationTextResolver): List<String> =
        if (AutomationLocalTime.parse(input) != null) {
            emptyList()
        } else {
            listOf(resolver.resolve(R.string.automation_definition_constraint_time_of_day_error_format))
        }
}
