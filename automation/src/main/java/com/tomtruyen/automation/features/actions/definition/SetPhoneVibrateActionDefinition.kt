package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.SetPhoneVibrateActionConfig

@GenerateActionDefinition
object SetPhoneVibrateActionDefinition : ActionDefinition<SetPhoneVibrateActionConfig>(
    configClass = SetPhoneVibrateActionConfig::class,
    defaultConfig = SetPhoneVibrateActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_set_phone_vibrate_title
    override val descriptionRes = R.string.automation_definition_action_set_phone_vibrate_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = SetPhoneVibrateActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_ENABLED,
            labelRes = R.string.automation_definition_action_set_phone_vibrate_field_state_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_action_set_phone_vibrate_field_state_description,
            defaultValue = VALUE_ON,
            options = listOf(
                AutomationOption(VALUE_ON, R.string.automation_definition_action_set_phone_vibrate_option_on),
                AutomationOption(VALUE_OFF, R.string.automation_definition_action_set_phone_vibrate_option_off),
            ),
            reader = { if (it.enabled) VALUE_ON else VALUE_OFF },
            updater = { config, value -> config.copy(enabled = value == VALUE_ON) },
        ),
    )

    override fun summarize(config: SetPhoneVibrateActionConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_action_set_phone_vibrate_summary,
        listOf(
            resolver.resolve(
                if (config.enabled) {
                    R.string.automation_definition_action_set_phone_vibrate_option_on
                } else {
                    R.string.automation_definition_action_set_phone_vibrate_option_off
                },
            ),
        ),
    )

    private const val FIELD_ENABLED = "enabled"
    private const val VALUE_ON = "on"
    private const val VALUE_OFF = "off"
}
