package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.triggers.config.BatteryLevelTriggerConfig

@GenerateTriggerDefinition
object BatteryLevelTriggerDefinition : TriggerDefinition<BatteryLevelTriggerConfig>(
    configClass = BatteryLevelTriggerConfig::class,
    defaultConfig = BatteryLevelTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_battery_level_title
    override val descriptionRes = R.string.automation_definition_trigger_battery_level_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = BatteryLevelTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_OPERATOR,
            labelRes = R.string.automation_definition_trigger_battery_level_field_operator_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_battery_level_field_operator_description,
            defaultValue = VALUE_LTE,
            options = listOf(
                AutomationOption(VALUE_GT, R.string.automation_definition_constraint_battery_level_option_gt),
                AutomationOption(VALUE_GTE, R.string.automation_definition_constraint_battery_level_option_gte),
                AutomationOption(VALUE_LT, R.string.automation_definition_constraint_battery_level_option_lt),
                AutomationOption(VALUE_LTE, R.string.automation_definition_constraint_battery_level_option_lte),
                AutomationOption(VALUE_EQ, R.string.automation_definition_constraint_battery_level_option_eq),
                AutomationOption(VALUE_NEQ, R.string.automation_definition_constraint_battery_level_option_neq),
            ),
            reader = { it.operator.toFieldValue() },
            updater = { config, value -> config.copy(operator = value.toOperator()) },
        ),
        TypedAutomationFieldDefinition(
            configClass = BatteryLevelTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_VALUE,
            labelRes = R.string.automation_definition_trigger_battery_level_field_value_label,
            type = AutomationFieldType.NUMBER,
            descriptionRes = R.string.automation_definition_trigger_battery_level_field_value_description,
            defaultValue = defaultConfig.value.toString(),
            placeholderRes = R.string.automation_definition_trigger_battery_level_field_value_placeholder,
            reader = { it.value.toString() },
            updater = { config, value -> config.copy(value = value.toIntOrNull() ?: defaultConfig.value) },
            inputValidator = { input, resolver ->
                if ((input.toIntOrNull() ?: -1) in 0..100) {
                    emptyList()
                } else {
                    listOf(resolver.resolve(R.string.automation_definition_trigger_battery_level_error_range))
                }
            },
        ),
    )

    override fun summarize(config: BatteryLevelTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_battery_level_summary,
            listOf(
                resolver.resolve(config.operator.labelRes),
                config.value,
            ),
        )

    private fun String.toOperator(): ComparisonOperator = when (this) {
        VALUE_GT -> ComparisonOperator.GREATER_THAN
        VALUE_GTE -> ComparisonOperator.GREATER_THAN_OR_EQUAL
        VALUE_LT -> ComparisonOperator.LESS_THAN
        VALUE_EQ -> ComparisonOperator.EQUAL
        VALUE_NEQ -> ComparisonOperator.NOT_EQUAL
        else -> ComparisonOperator.LESS_THAN_OR_EQUAL
    }

    private fun ComparisonOperator.toFieldValue(): String = when (this) {
        ComparisonOperator.GREATER_THAN -> VALUE_GT
        ComparisonOperator.GREATER_THAN_OR_EQUAL -> VALUE_GTE
        ComparisonOperator.LESS_THAN -> VALUE_LT
        ComparisonOperator.LESS_THAN_OR_EQUAL -> VALUE_LTE
        ComparisonOperator.EQUAL -> VALUE_EQ
        ComparisonOperator.NOT_EQUAL -> VALUE_NEQ
    }

    private val ComparisonOperator.labelRes: Int
        get() = when (this) {
            ComparisonOperator.GREATER_THAN -> R.string.automation_definition_constraint_battery_level_option_gt

            ComparisonOperator.GREATER_THAN_OR_EQUAL ->
                R.string.automation_definition_constraint_battery_level_option_gte

            ComparisonOperator.LESS_THAN -> R.string.automation_definition_constraint_battery_level_option_lt

            ComparisonOperator.LESS_THAN_OR_EQUAL ->
                R.string.automation_definition_constraint_battery_level_option_lte

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
}
