package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig

@GenerateTriggerDefinition
object NotificationReceivedTriggerDefinition : TriggerDefinition<NotificationReceivedTriggerConfig>(
    configClass = NotificationReceivedTriggerConfig::class,
    defaultConfig = NotificationReceivedTriggerConfig(),
) {
    override val isBeta = true
    override val titleRes = R.string.automation_definition_trigger_notification_received_title
    override val descriptionRes = R.string.automation_definition_trigger_notification_received_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = NotificationReceivedTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_PACKAGE,
            labelRes = R.string.automation_definition_trigger_notification_received_field_package_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_trigger_notification_received_field_package_description,
            defaultValue = "",
            placeholderRes = R.string.automation_definition_trigger_notification_received_field_package_placeholder,
            reader = { it.packageName },
            updater = { config, value -> config.copy(packageName = value.trim()) },
        ),
    )

    override fun validate(config: NotificationReceivedTriggerConfig, resolver: AutomationTextResolver): List<String> {
        val errors = super.validate(config, resolver).toMutableList()
        if (config.packageName.isBlank()) {
            errors += resolver.resolve(
                R.string.automation_definition_trigger_notification_received_error_missing_package,
            )
        }
        return errors
    }

    override fun summarize(config: NotificationReceivedTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_notification_received_summary,
            listOf(
                config.packageName.ifBlank {
                    resolver.resolve(
                        R.string.automation_definition_trigger_notification_received_unselected,
                    )
                },
            ),
        )

    private const val FIELD_PACKAGE = "package_name"
}
