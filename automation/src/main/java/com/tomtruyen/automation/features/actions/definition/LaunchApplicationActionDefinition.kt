package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig

@GenerateActionDefinition
object LaunchApplicationActionDefinition : ActionDefinition<LaunchApplicationActionConfig>(
    configClass = LaunchApplicationActionConfig::class,
    defaultConfig = LaunchApplicationActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_launch_application_title
    override val descriptionRes = R.string.automation_definition_action_launch_application_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = LaunchApplicationActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_PACKAGE_NAME,
            labelRes = R.string.automation_definition_action_launch_application_field_package_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_launch_application_field_package_description,
            defaultValue = defaultConfig.packageName,
            placeholderRes = R.string.automation_definition_action_launch_application_field_package_placeholder,
            reader = { it.packageName },
            updater = { config, value -> config.copy(packageName = value.trim()) },
        ),
    )

    override fun validate(config: LaunchApplicationActionConfig, resolver: AutomationTextResolver): List<String> =
        buildList {
            if (config.packageName.isBlank()) {
                add(resolver.resolve(R.string.automation_definition_action_launch_application_error_missing_package))
            }
        }

    override fun summarize(config: LaunchApplicationActionConfig, resolver: AutomationTextResolver): String = resolver.resolve(
        R.string.automation_definition_action_launch_application_summary,
        listOf(config.appLabel.ifBlank { config.packageName.ifBlank { resolver.resolve(R.string.automation_definition_action_launch_application_unselected) } }),
    )

    private const val FIELD_PACKAGE_NAME = "packageName"
}
