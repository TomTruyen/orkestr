package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.triggers.config.HeadphoneConnectionTriggerConfig

@GenerateTriggerDefinition
object HeadphoneConnectionTriggerDefinition : TriggerDefinition<HeadphoneConnectionTriggerConfig>(
    configClass = HeadphoneConnectionTriggerConfig::class,
    defaultConfig = HeadphoneConnectionTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_headphone_connection_title
    override val descriptionRes = R.string.automation_definition_trigger_headphone_connection_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = HeadphoneConnectionTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_CONNECTED,
            labelRes = R.string.automation_definition_trigger_connection_state_field_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_headphone_connection_field_description,
            defaultValue = VALUE_CONNECTED,
            options = connectionOptions(),
            reader = { it.connected.toConnectionFieldValue() },
            updater = { config, value -> config.copy(connected = value.toConnected()) },
        ),
    )

    override fun summarize(config: HeadphoneConnectionTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_headphone_connection_summary,
            listOf(resolver.resolve(config.connected.toConnectionLabelRes())),
        )
}
