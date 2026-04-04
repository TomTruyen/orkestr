package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig

object ShowNotificationActionDefinition : ActionDefinition<ShowNotificationActionConfig>(
    configClass = ShowNotificationActionConfig::class,
    defaultConfig = ShowNotificationActionConfig()
) {
    override val titleRes = R.string.automation_definition_action_show_notification_title
    override val descriptionRes = R.string.automation_definition_action_show_notification_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = ShowNotificationActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_TITLE,
            labelRes = R.string.automation_definition_action_show_notification_field_title_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_show_notification_field_title_description,
            defaultValue = ShowNotificationActionConfig().title,
            placeholderRes = R.string.automation_definition_action_show_notification_field_title_placeholder,
            reader = { it.title },
            updater = { config, value -> config.copy(title = value) }
        ),
        TypedAutomationFieldDefinition(
            configClass = ShowNotificationActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_MESSAGE,
            labelRes = R.string.automation_definition_action_show_notification_field_message_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_show_notification_field_message_description,
            defaultValue = ShowNotificationActionConfig().message,
            placeholderRes = R.string.automation_definition_action_show_notification_field_message_placeholder,
            reader = { it.message },
            updater = { config, value -> config.copy(message = value) }
        )
    )

    override fun summarize(config: ShowNotificationActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_show_notification_summary,
            listOf(config.title.ifBlank { defaultConfig.title })
        )

    private const val FIELD_TITLE = "title"
    private const val FIELD_MESSAGE = "message"
}
