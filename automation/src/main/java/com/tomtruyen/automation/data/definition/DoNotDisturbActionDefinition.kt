package com.tomtruyen.automation.data.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.features.actions.config.DoNotDisturbActionConfig

object DoNotDisturbActionDefinition : ActionDefinition<DoNotDisturbActionConfig>(
    configClass = DoNotDisturbActionConfig::class,
    defaultConfig = DoNotDisturbActionConfig()
) {
    override val titleRes = R.string.automation_definition_action_do_not_disturb_title
    override val descriptionRes = R.string.automation_definition_action_do_not_disturb_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = DoNotDisturbActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_MODE,
            labelRes = R.string.automation_definition_action_do_not_disturb_field_mode_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_action_do_not_disturb_field_mode_description,
            defaultValue = VALUE_PRIORITY_ONLY,
            options = listOf(
                AutomationOption(VALUE_PRIORITY_ONLY, R.string.automation_definition_action_do_not_disturb_option_priority_only),
                AutomationOption(VALUE_ALARMS_ONLY, R.string.automation_definition_action_do_not_disturb_option_alarms_only),
                AutomationOption(VALUE_TOTAL_SILENCE, R.string.automation_definition_action_do_not_disturb_option_total_silence),
                AutomationOption(VALUE_OFF, R.string.automation_definition_action_do_not_disturb_option_off)
            ),
            reader = { it.mode.toFieldValue() },
            updater = { config, value -> config.copy(mode = value.toMode()) }
        )
    )

    override fun summarize(config: DoNotDisturbActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_do_not_disturb_summary,
            listOf(resolver.resolve(config.mode.toLabelRes()))
        )

    private fun String.toMode(): DoNotDisturbMode = when (this) {
        VALUE_ALARMS_ONLY -> DoNotDisturbMode.ALARMS_ONLY
        VALUE_TOTAL_SILENCE -> DoNotDisturbMode.TOTAL_SILENCE
        VALUE_OFF -> DoNotDisturbMode.OFF
        else -> DoNotDisturbMode.PRIORITY_ONLY
    }

    private fun DoNotDisturbMode.toFieldValue(): String = when (this) {
        DoNotDisturbMode.PRIORITY_ONLY -> VALUE_PRIORITY_ONLY
        DoNotDisturbMode.ALARMS_ONLY -> VALUE_ALARMS_ONLY
        DoNotDisturbMode.TOTAL_SILENCE -> VALUE_TOTAL_SILENCE
        DoNotDisturbMode.OFF -> VALUE_OFF
    }

    private fun DoNotDisturbMode.toLabelRes(): Int = when (this) {
        DoNotDisturbMode.PRIORITY_ONLY -> R.string.automation_definition_action_do_not_disturb_option_priority_only
        DoNotDisturbMode.ALARMS_ONLY -> R.string.automation_definition_action_do_not_disturb_option_alarms_only
        DoNotDisturbMode.TOTAL_SILENCE -> R.string.automation_definition_action_do_not_disturb_option_total_silence
        DoNotDisturbMode.OFF -> R.string.automation_definition_action_do_not_disturb_option_off
    }

    private const val FIELD_MODE = "mode"
    private const val VALUE_PRIORITY_ONLY = "priority_only"
    private const val VALUE_ALARMS_ONLY = "alarms_only"
    private const val VALUE_TOTAL_SILENCE = "total_silence"
    private const val VALUE_OFF = "off"
}
