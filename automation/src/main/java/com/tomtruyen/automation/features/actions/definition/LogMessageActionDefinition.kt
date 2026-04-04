package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.data.definition.ActionDefinition
import com.tomtruyen.automation.data.definition.AutomationFieldType
import com.tomtruyen.automation.data.definition.AutomationTextResolver
import com.tomtruyen.automation.data.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig

object LogMessageActionDefinition : ActionDefinition<LogMessageActionConfig>(
    configClass = LogMessageActionConfig::class,
    defaultConfig = LogMessageActionConfig()
) {
    override val titleRes = R.string.automation_definition_action_log_message_title
    override val descriptionRes = R.string.automation_definition_action_log_message_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = LogMessageActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_MESSAGE,
            labelRes = R.string.automation_definition_action_log_message_field_message_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_log_message_field_message_description,
            defaultValue = defaultConfig.message,
            placeholderRes = R.string.automation_definition_action_log_message_field_message_placeholder,
            reader = { it.message },
            updater = { config, value -> config.copy(message = value) }
        )
    )

    override fun summarize(config: LogMessageActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_log_message_summary,
            listOf(config.message.ifBlank { defaultConfig.message })
        )

    private const val FIELD_MESSAGE = "message"
}
