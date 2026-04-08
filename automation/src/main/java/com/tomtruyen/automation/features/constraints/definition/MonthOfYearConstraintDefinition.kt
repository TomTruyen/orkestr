package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.MonthOfYear
import com.tomtruyen.automation.core.utils.SelectionCodec
import com.tomtruyen.automation.features.constraints.config.MonthOfYearConstraintConfig

@GenerateConstraintDefinition
object MonthOfYearConstraintDefinition : ConstraintDefinition<MonthOfYearConstraintConfig>(
    configClass = MonthOfYearConstraintConfig::class,
    defaultConfig = MonthOfYearConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_month_of_year_title
    override val descriptionRes = R.string.automation_definition_constraint_month_of_year_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = MonthOfYearConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "months",
            labelRes = R.string.automation_definition_constraint_month_of_year_field_label,
            type = AutomationFieldType.MULTI_CHOICE,
            descriptionRes = R.string.automation_definition_constraint_month_of_year_field_description,
            defaultValue = SelectionCodec.encode(defaultConfig.months.map(MonthOfYear::name)),
            choiceColumns = 3,
            options = MonthOfYear.entries.map { month ->
                AutomationOption(month.name, month.monthLabelRes())
            },
            reader = { config -> SelectionCodec.encode(config.months.map(MonthOfYear::name)) },
            updater = { config, value ->
                config.copy(
                    months = SelectionCodec.decode(value)
                        .mapNotNull { raw -> MonthOfYear.entries.find { it.name == raw } }
                        .toSet(),
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

    override fun summarize(config: MonthOfYearConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_month_of_year_summary,
            listOf(config.months.sortedBy { it.ordinal }.joinToString { resolver.resolve(it.monthLabelRes()) }),
        )

    override fun validate(config: MonthOfYearConstraintConfig, resolver: AutomationTextResolver): List<String> =
        if (config.months.isEmpty()) {
            listOf(resolver.resolve(R.string.automation_definition_constraint_selection_required))
        } else {
            super.validate(config, resolver)
        }
}
