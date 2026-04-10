package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageSeverity

@GenerateActionDefinition
object LogMessageActionDefinition : ActionDefinition<LogMessageActionConfig>(
    configClass = LogMessageActionConfig::class,
    defaultConfig = LogMessageActionConfig(),
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
            updater = { config, value -> config.copy(message = value) },
        ),
        TypedAutomationFieldDefinition(
            configClass = LogMessageActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_SEVERITY,
            labelRes = R.string.automation_definition_action_log_message_field_severity_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_action_log_message_field_severity_description,
            defaultValue = defaultConfig.severity.name,
            options = LogMessageSeverity.entries.map { severity ->
                AutomationOption(
                    value = severity.name,
                    labelRes = when (severity) {
                        LogMessageSeverity.DEBUG -> R.string.automation_definition_action_log_message_option_debug
                        LogMessageSeverity.INFO -> R.string.automation_definition_action_log_message_option_info
                        LogMessageSeverity.WARNING -> R.string.automation_definition_action_log_message_option_warning
                        LogMessageSeverity.ERROR -> R.string.automation_definition_action_log_message_option_error
                    },
                )
            },
            reader = { it.severity.name },
            updater = { config, value ->
                config.copy(
                    severity = LogMessageSeverity.entries.firstOrNull { it.name == value } ?: defaultConfig.severity,
                )
            },
        ),
    )

    override fun summarize(config: LogMessageActionConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_action_log_message_summary,
        listOf(
            config.message.ifBlank { defaultConfig.message },
            resolver.resolve(
                when (config.severity) {
                    LogMessageSeverity.DEBUG -> R.string.automation_definition_action_log_message_option_debug
                    LogMessageSeverity.INFO -> R.string.automation_definition_action_log_message_option_info
                    LogMessageSeverity.WARNING -> R.string.automation_definition_action_log_message_option_warning
                    LogMessageSeverity.ERROR -> R.string.automation_definition_action_log_message_option_error
                },
            ),
        ),
    )

    private const val FIELD_MESSAGE = "message"
    private const val FIELD_SEVERITY = "severity"
}
