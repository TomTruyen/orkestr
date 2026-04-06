package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.WifiRangeTriggerType
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig

@GenerateTriggerDefinition
object WifiSsidTriggerDefinition : TriggerDefinition<WifiSsidTriggerConfig>(
    configClass = WifiSsidTriggerConfig::class,
    defaultConfig = WifiSsidTriggerConfig(),
) {
    override val isBeta = true
    override val titleRes = R.string.automation_definition_trigger_wifi_ssid_title
    override val descriptionRes = R.string.automation_definition_trigger_wifi_ssid_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = WifiSsidTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_TRIGGER_TYPE,
            labelRes = R.string.automation_definition_trigger_wifi_ssid_field_trigger_type_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_wifi_ssid_field_trigger_type_description,
            defaultValue = VALUE_IN_RANGE,
            options = listOf(
                AutomationOption(VALUE_IN_RANGE, R.string.automation_definition_trigger_wifi_ssid_option_in_range),
                AutomationOption(
                    VALUE_OUT_OF_RANGE,
                    R.string.automation_definition_trigger_wifi_ssid_option_out_of_range,
                ),
            ),
            reader = { it.triggerType.toFieldValue() },
            updater = { config, value -> config.copy(triggerType = value.toTriggerType()) },
        ),
        TypedAutomationFieldDefinition(
            configClass = WifiSsidTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_SSID,
            labelRes = R.string.automation_definition_trigger_wifi_ssid_field_ssid_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_trigger_wifi_ssid_field_ssid_description,
            defaultValue = "",
            placeholderRes = R.string.automation_definition_trigger_wifi_ssid_field_ssid_placeholder,
            reader = { it.ssid },
            updater = { config, value -> config.copy(ssid = value.trim()) },
        ),
    )

    override fun validate(config: WifiSsidTriggerConfig, resolver: AutomationTextResolver): List<String> {
        val errors = super.validate(config, resolver).toMutableList()
        if (config.ssid.isBlank()) {
            errors += resolver.resolve(R.string.automation_definition_trigger_wifi_ssid_error_missing_ssid)
        }
        return errors
    }

    override fun summarize(config: WifiSsidTriggerConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_trigger_wifi_ssid_summary,
        listOf(
            resolver.resolve(
                when (config.triggerType) {
                    WifiRangeTriggerType.IN_RANGE -> R.string.automation_definition_trigger_wifi_ssid_option_in_range

                    WifiRangeTriggerType.OUT_OF_RANGE ->
                        R.string.automation_definition_trigger_wifi_ssid_option_out_of_range
                },
            ),
            config.ssid.ifBlank { resolver.resolve(R.string.automation_definition_trigger_wifi_ssid_unselected) },
        ),
    )

    private fun String.toTriggerType(): WifiRangeTriggerType = when (this) {
        VALUE_OUT_OF_RANGE -> WifiRangeTriggerType.OUT_OF_RANGE
        else -> WifiRangeTriggerType.IN_RANGE
    }

    private fun WifiRangeTriggerType.toFieldValue(): String = when (this) {
        WifiRangeTriggerType.IN_RANGE -> VALUE_IN_RANGE
        WifiRangeTriggerType.OUT_OF_RANGE -> VALUE_OUT_OF_RANGE
    }

    private const val FIELD_TRIGGER_TYPE = "trigger_type"
    private const val FIELD_SSID = "ssid"
    private const val VALUE_IN_RANGE = "in_range"
    private const val VALUE_OUT_OF_RANGE = "out_of_range"
}
