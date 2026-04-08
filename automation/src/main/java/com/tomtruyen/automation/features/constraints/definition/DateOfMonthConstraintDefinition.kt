package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.utils.SelectionCodec
import com.tomtruyen.automation.features.constraints.config.DateOfMonthConstraintConfig

@GenerateConstraintDefinition
object DateOfMonthConstraintDefinition : ConstraintDefinition<DateOfMonthConstraintConfig>(
    configClass = DateOfMonthConstraintConfig::class,
    defaultConfig = DateOfMonthConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_date_of_month_title
    override val descriptionRes = R.string.automation_definition_constraint_date_of_month_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = DateOfMonthConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "days",
            labelRes = R.string.automation_definition_constraint_date_of_month_field_label,
            type = AutomationFieldType.MULTI_CHOICE,
            descriptionRes = R.string.automation_definition_constraint_date_of_month_field_description,
            defaultValue = SelectionCodec.encode(defaultConfig.days.map(Int::toString)),
            choiceColumns = 7,
            options = (1..31).map { day ->
                AutomationOption(day.toString(), dayOfMonthLabelRes(day))
            },
            reader = { config -> SelectionCodec.encode(config.days.map(Int::toString)) },
            updater = { config, value ->
                config.copy(
                    days = SelectionCodec.decode(value).mapNotNull(String::toIntOrNull).toSet(),
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

    override fun summarize(config: DateOfMonthConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_date_of_month_summary,
            listOf(config.days.sorted().joinToString()),
        )

    override fun validate(config: DateOfMonthConstraintConfig, resolver: AutomationTextResolver): List<String> =
        if (config.days.isEmpty()) {
            listOf(resolver.resolve(R.string.automation_definition_constraint_selection_required))
        } else {
            super.validate(config, resolver)
        }
}
