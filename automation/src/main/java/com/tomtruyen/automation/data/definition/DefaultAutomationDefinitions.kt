package com.tomtruyen.automation.data.definition

import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

// TODO: Extract this to some other place under the actual feature and using not a Map<String, String> but the actual config type via a Generic
object ChargeStateTriggerDefinition : TriggerDefinition {
    override val type = TriggerType.CHARGE_STATE
    override val title = "Charge State"
    override val description = "Runs when the device enters a matching charging state."
    override val fields = listOf(
        AutomationFieldDefinition(
            id = "state",
            label = "Charge State",
            type = AutomationFieldType.SINGLE_CHOICE,
            description = "The charging state that should trigger the rule.",
            defaultValue = "charging",
            options = listOf(
                AutomationOption("charging", "Charging"),
                AutomationOption("discharging", "Discharging"),
                AutomationOption("full", "Full"),
                AutomationOption("not_charging", "Not charging")
            )
        )
    )

    override fun createConfig(values: Map<String, String>) = BatteryChangedTriggerConfig(
        state = when (normalized(values)["state"]) {
            "discharging" -> BatteryChargeState.DISCHARGING
            "full" -> BatteryChargeState.FULL
            "not_charging" -> BatteryChargeState.NOT_CHARGING
            else -> BatteryChargeState.CHARGING
        }
    )

    override fun valuesOf(config: TriggerConfig): Map<String, String> {
        val typed = config as? BatteryChangedTriggerConfig ?: BatteryChangedTriggerConfig()
        return mapOf(
            "state" to when (typed.state) {
                BatteryChargeState.CHARGING -> "charging"
                BatteryChargeState.DISCHARGING -> "discharging"
                BatteryChargeState.FULL -> "full"
                BatteryChargeState.NOT_CHARGING -> "not_charging"
                BatteryChargeState.UNKNOWN -> "charging"
            }
        )
    }

    override fun summarize(values: Map<String, String>): String {
        val option = fields.first().options.firstOrNull { it.value == values["state"] }?.label ?: "Charging"
        return "When charge state becomes $option"
    }
}

object BatteryLevelConstraintDefinition : ConstraintDefinition {
    override val type = ConstraintType.BATTERY_LEVEL
    override val title = "Battery Level"
    override val description = "Only allows the rule to continue when the battery percentage matches."
    override val fields = listOf(
        AutomationFieldDefinition(
            id = "operator",
            label = "Operator",
            type = AutomationFieldType.SINGLE_CHOICE,
            description = "How the current battery percentage should be compared.",
            defaultValue = "gte",
            options = listOf(
                AutomationOption("gt", "Greater than"),
                AutomationOption("gte", "At least"),
                AutomationOption("lt", "Less than"),
                AutomationOption("lte", "At most"),
                AutomationOption("eq", "Exactly"),
                AutomationOption("neq", "Not equal")
            )
        ),
        AutomationFieldDefinition(
            id = "value",
            label = "Battery %",
            type = AutomationFieldType.NUMBER,
            description = "Battery percentage to compare against.",
            defaultValue = "80",
            placeholder = "80"
        )
    )

    override fun createConfig(values: Map<String, String>) = BatteryLevelConstraintConfig(
        operator = when (normalized(values)["operator"]) {
            "gt" -> ComparisonOperator.GREATER_THAN
            "lt" -> ComparisonOperator.LESS_THAN
            "lte" -> ComparisonOperator.LESS_THAN_OR_EQUAL
            "eq" -> ComparisonOperator.EQUAL
            "neq" -> ComparisonOperator.NOT_EQUAL
            else -> ComparisonOperator.GREATER_THAN_OR_EQUAL
        },
        value = normalized(values)["value"]?.toIntOrNull() ?: 80
    )

    override fun valuesOf(config: ConstraintConfig): Map<String, String> {
        val typed = config as? BatteryLevelConstraintConfig ?: BatteryLevelConstraintConfig()
        return mapOf(
            "operator" to when (typed.operator) {
                ComparisonOperator.GREATER_THAN -> "gt"
                ComparisonOperator.GREATER_THAN_OR_EQUAL -> "gte"
                ComparisonOperator.LESS_THAN -> "lt"
                ComparisonOperator.LESS_THAN_OR_EQUAL -> "lte"
                ComparisonOperator.EQUAL -> "eq"
                ComparisonOperator.NOT_EQUAL -> "neq"
            },
            "value" to typed.value.toString()
        )
    }

    override fun summarize(values: Map<String, String>): String {
        val operator = when (values["operator"]) {
            "gt" -> "is greater than"
            "lt" -> "is less than"
            "lte" -> "is at most"
            "eq" -> "is exactly"
            "neq" -> "is not equal to"
            else -> "is at least"
        }
        val value = values["value"].orEmpty().ifBlank { "80" }
        return "Only if battery $operator $value%"
    }

    override fun validate(values: Map<String, String>): List<String> {
        val errors = super.validate(values).toMutableList()
        val value = values["value"]?.toIntOrNull()
        if (value != null && value !in 0..100) {
            errors += "Battery % must be between 0 and 100."
        }
        return errors
    }
}

object ShowNotificationActionDefinition : ActionDefinition {
    override val type = ActionType.SHOW_NOTIFICATION
    override val title = "Show Notification"
    override val description = "Posts a simple notification when the rule finishes."
    override val fields = listOf(
        AutomationFieldDefinition(
            id = "title",
            label = "Title",
            type = AutomationFieldType.TEXT,
            description = "Notification title.",
            defaultValue = "Automation started",
            placeholder = "Automation started"
        ),
        AutomationFieldDefinition(
            id = "message",
            label = "Message",
            type = AutomationFieldType.TEXT,
            description = "Notification body.",
            defaultValue = "Your rule was triggered.",
            placeholder = "Your rule was triggered."
        )
    )

    override fun createConfig(values: Map<String, String>) = ShowNotificationActionConfig(
        title = normalized(values)["title"].orEmpty(),
        message = normalized(values)["message"].orEmpty()
    )

    override fun valuesOf(config: ActionConfig): Map<String, String> {
        val typed = config as? ShowNotificationActionConfig ?: ShowNotificationActionConfig()
        return mapOf(
            "title" to typed.title,
            "message" to typed.message
        )
    }

    override fun summarize(values: Map<String, String>): String {
        val title = values["title"].orEmpty().ifBlank { "Automation started" }
        return "Show notification \"$title\""
    }
}

object LogMessageActionDefinition : ActionDefinition {
    override val type = ActionType.LOG_MESSAGE
    override val title = "Log Message"
    override val description = "Writes a custom message to the automation log."
    override val fields = listOf(
        AutomationFieldDefinition(
            id = "message",
            label = "Message",
            type = AutomationFieldType.TEXT,
            description = "Message written to the automation log.",
            defaultValue = "Rule executed",
            placeholder = "Rule executed"
        )
    )

    override fun createConfig(values: Map<String, String>) = LogMessageActionConfig(
        message = normalized(values)["message"].orEmpty()
    )

    override fun valuesOf(config: ActionConfig): Map<String, String> {
        val typed = config as? LogMessageActionConfig ?: LogMessageActionConfig()
        return mapOf("message" to typed.message)
    }

    override fun summarize(values: Map<String, String>): String {
        val message = values["message"].orEmpty().ifBlank { "Rule executed" }
        return "Log \"$message\""
    }
}
