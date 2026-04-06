package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.triggers.config.BatterySaverStateTriggerConfig

@GenerateTriggerDefinition
object BatterySaverStateTriggerDefinition : TriggerDefinition<BatterySaverStateTriggerConfig>(
    configClass = BatterySaverStateTriggerConfig::class,
    defaultConfig = BatterySaverStateTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_battery_saver_title
    override val descriptionRes = R.string.automation_definition_trigger_battery_saver_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = BatterySaverStateTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_ENABLED,
            labelRes = R.string.automation_definition_trigger_battery_saver_field_enabled_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_trigger_battery_saver_field_enabled_description,
            defaultValue = true.toString(),
            reader = { it.enabled.toString() },
            updater = { config, value -> config.copy(enabled = value.toBoolean()) },
        ),
    )

    override fun summarize(config: BatterySaverStateTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_battery_saver_summary,
            listOf(
                resolver.resolve(
                    if (config.enabled) {
                        R.string.automation_definition_trigger_battery_saver_option_on
                    } else {
                        R.string.automation_definition_trigger_battery_saver_option_off
                    },
                ),
            ),
        )

    private const val FIELD_ENABLED = "enabled"
}
