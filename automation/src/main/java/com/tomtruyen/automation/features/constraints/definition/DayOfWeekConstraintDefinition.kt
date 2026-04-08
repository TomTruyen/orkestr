package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.core.utils.SelectionCodec
import com.tomtruyen.automation.features.constraints.config.DayOfWeekConstraintConfig

@GenerateConstraintDefinition
object DayOfWeekConstraintDefinition : ConstraintDefinition<DayOfWeekConstraintConfig>(
    configClass = DayOfWeekConstraintConfig::class,
    defaultConfig = DayOfWeekConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_day_of_week_title
    override val descriptionRes = R.string.automation_definition_constraint_day_of_week_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = DayOfWeekConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "days",
            labelRes = R.string.automation_definition_constraint_day_of_week_field_label,
            type = AutomationFieldType.MULTI_CHOICE,
            descriptionRes = R.string.automation_definition_constraint_day_of_week_field_description,
            defaultValue = SelectionCodec.encode(defaultConfig.days.map(Weekday::name)),
            choiceColumns = 4,
            options = Weekday.entries.map { day ->
                AutomationOption(day.name, day.weekdayLabelRes())
            },
            reader = { config -> SelectionCodec.encode(config.days.map(Weekday::name)) },
            updater = { config, value ->
                config.copy(
                    days = SelectionCodec.decode(
                        value,
                    ).mapNotNull { raw -> Weekday.entries.find { it.name == raw } }.toSet(),
                )
            },
            inputValidator = { input, resolver ->
                if (SelectionCodec.decode(input).isEmpty()) {
                    listOf(resolver.resolve(R.string.automation_definition_constraint_selection_required))
                } else {
                    emptyList()
                }
            },
        ),
    )

    override fun summarize(config: DayOfWeekConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_day_of_week_summary,
            listOf(config.days.sortedBy { it.ordinal }.joinToString { resolver.resolve(it.weekdayLabelRes()) }),
        )

    override fun validate(config: DayOfWeekConstraintConfig, resolver: AutomationTextResolver): List<String> =
        if (config.days.isEmpty()) {
            listOf(resolver.resolve(R.string.automation_definition_constraint_selection_required))
        } else {
            super.validate(config, resolver)
        }
}
