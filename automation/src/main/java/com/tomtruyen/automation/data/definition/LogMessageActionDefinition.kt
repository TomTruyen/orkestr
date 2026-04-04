package com.tomtruyen.automation.data.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig

object LogMessageActionDefinition : ActionDefinition<LogMessageActionConfig>(
    configClass = LogMessageActionConfig::class,
    defaultConfig = LogMessageActionConfig()
) {
    override val titleRes = R.string.automation_definition_action_log_message_title
    override val descriptionRes = R.string.automation_definition_action_log_message_description
    override val fields = listOf(
        AutomationFieldDefinition(
            id = FIELD_MESSAGE,
            labelRes = R.string.automation_definition_action_log_message_field_message_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_log_message_field_message_description,
            defaultValue = defaultConfig.message,
            placeholderRes = R.string.automation_definition_action_log_message_field_message_placeholder
        )
    )

    override fun updateField(config: LogMessageActionConfig, fieldId: String, value: String): LogMessageActionConfig =
        when (fieldId) {
            FIELD_MESSAGE -> config.copy(message = value)
            else -> config
        }

    override fun valuesOf(config: LogMessageActionConfig): Map<String, String> = mapOf(
        FIELD_MESSAGE to config.message
    )

    override fun summarize(config: LogMessageActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_log_message_summary,
            listOf(config.message.ifBlank { defaultConfig.message })
        )

    private const val FIELD_MESSAGE = "message"
}
