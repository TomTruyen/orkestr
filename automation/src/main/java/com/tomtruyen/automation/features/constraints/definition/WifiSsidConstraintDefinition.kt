package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig

@GenerateConstraintDefinition
object WifiSsidConstraintDefinition : ConstraintDefinition<WifiSsidConstraintConfig>(
    configClass = WifiSsidConstraintConfig::class,
    defaultConfig = WifiSsidConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_wifi_ssid_title
    override val descriptionRes = R.string.automation_definition_constraint_wifi_ssid_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = WifiSsidConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "ssid",
            labelRes = R.string.automation_definition_constraint_wifi_ssid_field_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_constraint_wifi_ssid_field_description,
            required = false,
            defaultValue = "",
            placeholderRes = R.string.automation_definition_trigger_wifi_ssid_field_ssid_placeholder,
            reader = { it.ssid },
            updater = { config, value -> config.copy(ssid = value.trim()) },
        ),
    )

    override fun validate(config: WifiSsidConstraintConfig, resolver: AutomationTextResolver): List<String> {
        val errors = super.validate(config, resolver).toMutableList()
        if (config.ssid.isBlank()) {
            errors += resolver.resolve(R.string.automation_definition_constraint_wifi_ssid_error_missing_ssid)
        }
        return errors
    }

    override fun summarize(config: WifiSsidConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_wifi_ssid_summary,
            listOf(
                config.ssid.ifBlank { resolver.resolve(R.string.automation_definition_trigger_wifi_ssid_unselected) },
            ),
        )
}
