package com.tomtruyen.automation.data.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

object ShowNotificationActionDefinition : ActionDefinition<ShowNotificationActionConfig>(
    configClass = ShowNotificationActionConfig::class,
    defaultConfig = ShowNotificationActionConfig()
) {
    override val titleRes = R.string.automation_definition_action_show_notification_title
    override val descriptionRes = R.string.automation_definition_action_show_notification_description
    override val fields = listOf(
        AutomationFieldDefinition(
            id = FIELD_TITLE,
            labelRes = R.string.automation_definition_action_show_notification_field_title_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_show_notification_field_title_description,
            defaultValue = ShowNotificationActionConfig().title,
            placeholderRes = R.string.automation_definition_action_show_notification_field_title_placeholder
        ),
        AutomationFieldDefinition(
            id = FIELD_MESSAGE,
            labelRes = R.string.automation_definition_action_show_notification_field_message_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_show_notification_field_message_description,
            defaultValue = ShowNotificationActionConfig().message,
            placeholderRes = R.string.automation_definition_action_show_notification_field_message_placeholder
        )
    )

    override fun updateField(config: ShowNotificationActionConfig, fieldId: String, value: String): ShowNotificationActionConfig =
        when (fieldId) {
            FIELD_TITLE -> config.copy(title = value)
            FIELD_MESSAGE -> config.copy(message = value)
            else -> config
        }

    override fun valuesOf(config: ShowNotificationActionConfig): Map<String, String> = mapOf(
        FIELD_TITLE to config.title,
        FIELD_MESSAGE to config.message
    )

    override fun summarize(config: ShowNotificationActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_show_notification_summary,
            listOf(config.title.ifBlank { defaultConfig.title })
        )

    private const val FIELD_TITLE = "title"
    private const val FIELD_MESSAGE = "message"
}
