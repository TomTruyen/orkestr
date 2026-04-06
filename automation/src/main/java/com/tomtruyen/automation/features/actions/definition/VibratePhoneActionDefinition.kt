package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.VibratePhoneActionConfig

@GenerateActionDefinition
object VibratePhoneActionDefinition : ActionDefinition<VibratePhoneActionConfig>(
    configClass = VibratePhoneActionConfig::class,
    defaultConfig = VibratePhoneActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_vibrate_phone_title
    override val descriptionRes = R.string.automation_definition_action_vibrate_phone_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = VibratePhoneActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_DURATION_MILLIS,
            labelRes = R.string.automation_definition_action_vibrate_phone_field_duration_label,
            type = AutomationFieldType.NUMBER,
            descriptionRes = R.string.automation_definition_action_vibrate_phone_field_duration_description,
            defaultValue = defaultConfig.durationMillis.toString(),
            placeholderRes = R.string.automation_definition_action_vibrate_phone_field_duration_placeholder,
            reader = { it.durationMillis.toString() },
            updater = { config, value -> config.copy(durationMillis = value.toIntOrNull() ?: defaultConfig.durationMillis) },
        ),
    )

    override fun validate(config: VibratePhoneActionConfig, resolver: AutomationTextResolver): List<String> =
        buildList {
            if (config.durationMillis !in 100..10_000) {
                add(resolver.resolve(R.string.automation_definition_action_vibrate_phone_error_range))
            }
        }

    override fun summarize(config: VibratePhoneActionConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_action_vibrate_phone_summary,
        listOf(config.durationMillis),
    )

    private const val FIELD_DURATION_MILLIS = "durationMillis"
}
