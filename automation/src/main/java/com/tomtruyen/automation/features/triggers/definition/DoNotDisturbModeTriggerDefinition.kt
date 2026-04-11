package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.features.triggers.config.DoNotDisturbModeTriggerConfig

@GenerateTriggerDefinition
object DoNotDisturbModeTriggerDefinition : TriggerDefinition<DoNotDisturbModeTriggerConfig>(
    configClass = DoNotDisturbModeTriggerConfig::class,
    defaultConfig = DoNotDisturbModeTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_dnd_mode_title
    override val descriptionRes = R.string.automation_definition_trigger_dnd_mode_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = DoNotDisturbModeTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_MODE,
            labelRes = R.string.automation_definition_trigger_dnd_mode_field_mode_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_dnd_mode_field_mode_description,
            defaultValue = VALUE_OFF,
            options = listOf(
                AutomationOption(VALUE_OFF, R.string.automation_definition_trigger_dnd_mode_option_off),
                AutomationOption(VALUE_PRIORITY, R.string.automation_definition_trigger_dnd_mode_option_priority),
                AutomationOption(VALUE_ALARMS, R.string.automation_definition_trigger_dnd_mode_option_alarms),
                AutomationOption(VALUE_SILENCE, R.string.automation_definition_trigger_dnd_mode_option_silence),
            ),
            reader = { it.mode.toFieldValue() },
            updater = { config, value -> config.copy(mode = value.toDoNotDisturbMode()) },
        ),
    )

    override fun summarize(config: DoNotDisturbModeTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_dnd_mode_summary,
            listOf(resolver.resolve(config.mode.toLabelRes())),
        )

    private fun DoNotDisturbMode.toFieldValue(): String = when (this) {
        DoNotDisturbMode.OFF -> VALUE_OFF
        DoNotDisturbMode.PRIORITY_ONLY -> VALUE_PRIORITY
        DoNotDisturbMode.ALARMS_ONLY -> VALUE_ALARMS
        DoNotDisturbMode.TOTAL_SILENCE -> VALUE_SILENCE
    }

    private fun String.toDoNotDisturbMode(): DoNotDisturbMode = when (this) {
        VALUE_PRIORITY -> DoNotDisturbMode.PRIORITY_ONLY
        VALUE_ALARMS -> DoNotDisturbMode.ALARMS_ONLY
        VALUE_SILENCE -> DoNotDisturbMode.TOTAL_SILENCE
        else -> DoNotDisturbMode.OFF
    }

    private fun DoNotDisturbMode.toLabelRes(): Int = when (this) {
        DoNotDisturbMode.OFF -> R.string.automation_definition_trigger_dnd_mode_option_off
        DoNotDisturbMode.PRIORITY_ONLY -> R.string.automation_definition_trigger_dnd_mode_option_priority
        DoNotDisturbMode.ALARMS_ONLY -> R.string.automation_definition_trigger_dnd_mode_option_alarms
        DoNotDisturbMode.TOTAL_SILENCE -> R.string.automation_definition_trigger_dnd_mode_option_silence
    }

    private const val FIELD_MODE = "mode"
    private const val VALUE_OFF = "off"
    private const val VALUE_PRIORITY = "priority"
    private const val VALUE_ALARMS = "alarms"
    private const val VALUE_SILENCE = "silence"
}
