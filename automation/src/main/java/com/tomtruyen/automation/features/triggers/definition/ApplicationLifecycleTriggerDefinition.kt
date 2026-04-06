package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig

@GenerateTriggerDefinition
object ApplicationLifecycleTriggerDefinition : TriggerDefinition<ApplicationLifecycleTriggerConfig>(
    configClass = ApplicationLifecycleTriggerConfig::class,
    defaultConfig = ApplicationLifecycleTriggerConfig(),
) {
    override val isBeta = true
    override val titleRes = R.string.automation_definition_trigger_application_lifecycle_title
    override val descriptionRes = R.string.automation_definition_trigger_application_lifecycle_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = ApplicationLifecycleTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_PACKAGE,
            labelRes = R.string.automation_definition_trigger_application_lifecycle_field_package_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_trigger_application_lifecycle_field_package_description,
            defaultValue = "",
            placeholderRes = R.string.automation_definition_trigger_application_lifecycle_field_package_placeholder,
            reader = { it.packageName },
            updater = { config, value -> config.copy(packageName = value.trim()) },
        ),
        TypedAutomationFieldDefinition(
            configClass = ApplicationLifecycleTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_EVENT,
            labelRes = R.string.automation_definition_trigger_application_lifecycle_field_event_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_application_lifecycle_field_event_description,
            defaultValue = VALUE_LAUNCHED,
            options = listOf(
                AutomationOption(
                    VALUE_LAUNCHED,
                    R.string.automation_definition_trigger_application_lifecycle_option_launched,
                ),
                AutomationOption(
                    VALUE_CLOSED,
                    R.string.automation_definition_trigger_application_lifecycle_option_closed,
                ),
            ),
            reader = { it.transitionType.toFieldValue() },
            updater = { config, value -> config.copy(transitionType = value.toTransitionType()) },
        ),
    )

    override fun validate(config: ApplicationLifecycleTriggerConfig, resolver: AutomationTextResolver): List<String> {
        val errors = super.validate(config, resolver).toMutableList()
        if (config.packageName.isBlank()) {
            errors += resolver.resolve(
                R.string.automation_definition_trigger_application_lifecycle_error_missing_package,
            )
        }
        return errors
    }

    override fun summarize(config: ApplicationLifecycleTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_application_lifecycle_summary,
            listOf(
                config.packageName.ifBlank {
                    resolver.resolve(
                        R.string.automation_definition_trigger_application_lifecycle_unselected,
                    )
                },
                resolver.resolve(
                    when (config.transitionType) {
                        AppLifecycleTransitionType.LAUNCHED ->
                            R.string.automation_definition_trigger_application_lifecycle_option_launched

                        AppLifecycleTransitionType.CLOSED ->
                            R.string.automation_definition_trigger_application_lifecycle_option_closed
                    },
                ),
            ),
        )

    private fun String.toTransitionType(): AppLifecycleTransitionType = when (this) {
        VALUE_CLOSED -> AppLifecycleTransitionType.CLOSED
        else -> AppLifecycleTransitionType.LAUNCHED
    }

    private fun AppLifecycleTransitionType.toFieldValue(): String = when (this) {
        AppLifecycleTransitionType.LAUNCHED -> VALUE_LAUNCHED
        AppLifecycleTransitionType.CLOSED -> VALUE_CLOSED
    }

    private const val FIELD_PACKAGE = "package_name"
    private const val FIELD_EVENT = "event"
    private const val VALUE_LAUNCHED = "launched"
    private const val VALUE_CLOSED = "closed"
}
