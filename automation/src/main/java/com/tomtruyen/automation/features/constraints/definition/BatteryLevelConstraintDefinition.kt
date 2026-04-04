package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.data.definition.AutomationFieldType
import com.tomtruyen.automation.data.definition.AutomationOption
import com.tomtruyen.automation.data.definition.AutomationTextResolver
import com.tomtruyen.automation.data.definition.ConstraintDefinition
import com.tomtruyen.automation.data.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig

object BatteryLevelConstraintDefinition : ConstraintDefinition<BatteryLevelConstraintConfig>(
    configClass = BatteryLevelConstraintConfig::class,
    defaultConfig = BatteryLevelConstraintConfig()
) {
    override val titleRes = R.string.automation_definition_constraint_battery_level_title
    override val descriptionRes = R.string.automation_definition_constraint_battery_level_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = BatteryLevelConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_OPERATOR,
            labelRes = R.string.automation_definition_constraint_battery_level_field_operator_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_constraint_battery_level_field_operator_description,
            defaultValue = VALUE_GTE,
            options = listOf(
                AutomationOption(
                    VALUE_GT,
                    R.string.automation_definition_constraint_battery_level_option_gt
                ),
                AutomationOption(
                    VALUE_GTE,
                    R.string.automation_definition_constraint_battery_level_option_gte
                ),
                AutomationOption(
                    VALUE_LT,
                    R.string.automation_definition_constraint_battery_level_option_lt
                ),
                AutomationOption(
                    VALUE_LTE,
                    R.string.automation_definition_constraint_battery_level_option_lte
                ),
                AutomationOption(
                    VALUE_EQ,
                    R.string.automation_definition_constraint_battery_level_option_eq
                ),
                AutomationOption(
                    VALUE_NEQ,
                    R.string.automation_definition_constraint_battery_level_option_neq
                )
            ),
            reader = { it.operator.toFieldValue() },
            updater = { config, value -> config.copy(operator = value.toOperator()) }
        ),
        TypedAutomationFieldDefinition(
            configClass = BatteryLevelConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_VALUE,
            labelRes = R.string.automation_definition_constraint_battery_level_field_value_label,
            type = AutomationFieldType.NUMBER,
            descriptionRes = R.string.automation_definition_constraint_battery_level_field_value_description,
            defaultValue = DEFAULT_BATTERY_VALUE,
            placeholderRes = R.string.automation_definition_constraint_battery_level_field_value_placeholder,
            reader = { it.value.toString() },
            updater = { config, value ->
                if (value.isBlank()) {
                    config.copy(value = DEFAULT_BATTERY_VALUE.toInt())
                } else {
                    value.toIntOrNull()?.let { parsed -> config.copy(value = parsed) } ?: config
                }
            },
            inputValidator = { input, resolver ->
                val value = input.toIntOrNull()
                if (value != null && value !in 0..100) {
                    listOf(resolver.resolve(R.string.automation_definition_constraint_battery_level_error_range))
                } else {
                    emptyList()
                }
            }
        )
    )

    override fun summarize(config: BatteryLevelConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_battery_level_summary,
            listOf(
                resolver.resolve(config.operator.toLabelRes()),
                config.value
            )
        )

    private fun String.toOperator(): ComparisonOperator = when (this) {
        VALUE_GT -> ComparisonOperator.GREATER_THAN
        VALUE_LT -> ComparisonOperator.LESS_THAN
        VALUE_LTE -> ComparisonOperator.LESS_THAN_OR_EQUAL
        VALUE_EQ -> ComparisonOperator.EQUAL
        VALUE_NEQ -> ComparisonOperator.NOT_EQUAL
        else -> ComparisonOperator.GREATER_THAN_OR_EQUAL
    }

    private fun ComparisonOperator.toFieldValue(): String = when (this) {
        ComparisonOperator.GREATER_THAN -> VALUE_GT
        ComparisonOperator.GREATER_THAN_OR_EQUAL -> VALUE_GTE
        ComparisonOperator.LESS_THAN -> VALUE_LT
        ComparisonOperator.LESS_THAN_OR_EQUAL -> VALUE_LTE
        ComparisonOperator.EQUAL -> VALUE_EQ
        ComparisonOperator.NOT_EQUAL -> VALUE_NEQ
    }

    private fun ComparisonOperator.toLabelRes(): Int = when (this) {
        ComparisonOperator.GREATER_THAN -> R.string.automation_definition_constraint_battery_level_option_gt
        ComparisonOperator.GREATER_THAN_OR_EQUAL -> R.string.automation_definition_constraint_battery_level_option_gte
        ComparisonOperator.LESS_THAN -> R.string.automation_definition_constraint_battery_level_option_lt
        ComparisonOperator.LESS_THAN_OR_EQUAL -> R.string.automation_definition_constraint_battery_level_option_lte
        ComparisonOperator.EQUAL -> R.string.automation_definition_constraint_battery_level_option_eq
        ComparisonOperator.NOT_EQUAL -> R.string.automation_definition_constraint_battery_level_option_neq
    }

    private const val FIELD_OPERATOR = "operator"
    private const val FIELD_VALUE = "value"
    private const val VALUE_GT = "gt"
    private const val VALUE_GTE = "gte"
    private const val VALUE_LT = "lt"
    private const val VALUE_LTE = "lte"
    private const val VALUE_EQ = "eq"
    private const val VALUE_NEQ = "neq"
    private const val DEFAULT_BATTERY_VALUE = "80"
}