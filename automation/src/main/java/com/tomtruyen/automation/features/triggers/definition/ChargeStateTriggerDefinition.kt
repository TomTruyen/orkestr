package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.data.definition.AutomationFieldType
import com.tomtruyen.automation.data.definition.AutomationOption
import com.tomtruyen.automation.data.definition.AutomationTextResolver
import com.tomtruyen.automation.data.definition.TriggerDefinition
import com.tomtruyen.automation.data.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig

object ChargeStateTriggerDefinition : TriggerDefinition<BatteryChangedTriggerConfig>(
    configClass = BatteryChangedTriggerConfig::class,
    defaultConfig = BatteryChangedTriggerConfig()
) {
    override val titleRes = R.string.automation_definition_trigger_charge_state_title
    override val descriptionRes = R.string.automation_definition_trigger_charge_state_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = BatteryChangedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_STATE,
            labelRes = R.string.automation_definition_trigger_charge_state_field_state_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_charge_state_field_state_description,
            defaultValue = VALUE_CHARGING,
            options = listOf(
                AutomationOption(
                    VALUE_CHARGING,
                    R.string.automation_definition_trigger_charge_state_option_charging
                ),
                AutomationOption(
                    VALUE_DISCHARGING,
                    R.string.automation_definition_trigger_charge_state_option_discharging
                ),
                AutomationOption(
                    VALUE_FULL,
                    R.string.automation_definition_trigger_charge_state_option_full
                ),
                AutomationOption(
                    VALUE_NOT_CHARGING,
                    R.string.automation_definition_trigger_charge_state_option_not_charging
                )
            ),
            reader = { it.state.toFieldValue() },
            updater = { config, value -> config.copy(state = value.toChargeState()) }
        )
    )

    override fun summarize(config: BatteryChangedTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_charge_state_summary,
            listOf(resolver.resolve(config.state.toLabelRes()))
        )

    private fun String.toChargeState(): BatteryChargeState = when (this) {
        VALUE_DISCHARGING -> BatteryChargeState.DISCHARGING
        VALUE_FULL -> BatteryChargeState.FULL
        VALUE_NOT_CHARGING -> BatteryChargeState.NOT_CHARGING
        else -> BatteryChargeState.CHARGING
    }

    private fun BatteryChargeState.toFieldValue(): String = when (this) {
        BatteryChargeState.CHARGING -> VALUE_CHARGING
        BatteryChargeState.DISCHARGING -> VALUE_DISCHARGING
        BatteryChargeState.FULL -> VALUE_FULL
        BatteryChargeState.NOT_CHARGING -> VALUE_NOT_CHARGING
        BatteryChargeState.UNKNOWN -> VALUE_CHARGING
    }

    private fun BatteryChargeState.toLabelRes(): Int = when (this) {
        BatteryChargeState.CHARGING -> R.string.automation_definition_trigger_charge_state_option_charging
        BatteryChargeState.DISCHARGING -> R.string.automation_definition_trigger_charge_state_option_discharging
        BatteryChargeState.FULL -> R.string.automation_definition_trigger_charge_state_option_full
        BatteryChargeState.NOT_CHARGING -> R.string.automation_definition_trigger_charge_state_option_not_charging
        BatteryChargeState.UNKNOWN -> R.string.automation_definition_trigger_charge_state_option_charging
    }

    private const val FIELD_STATE = "state"
    private const val VALUE_CHARGING = "charging"
    private const val VALUE_DISCHARGING = "discharging"
    private const val VALUE_FULL = "full"
    private const val VALUE_NOT_CHARGING = "not_charging"
}