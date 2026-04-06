package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.OpenWebsiteActionConfig

@GenerateActionDefinition
object OpenWebsiteActionDefinition : ActionDefinition<OpenWebsiteActionConfig>(
    configClass = OpenWebsiteActionConfig::class,
    defaultConfig = OpenWebsiteActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_open_website_title
    override val descriptionRes = R.string.automation_definition_action_open_website_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = OpenWebsiteActionConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_URL,
            labelRes = R.string.automation_definition_action_open_website_field_url_label,
            type = AutomationFieldType.TEXT,
            descriptionRes = R.string.automation_definition_action_open_website_field_url_description,
            defaultValue = defaultConfig.url,
            placeholderRes = R.string.automation_definition_action_open_website_field_url_placeholder,
            reader = { it.url },
            updater = { config, value -> config.copy(url = value.trim()) },
        ),
    )

    override fun validate(config: OpenWebsiteActionConfig, resolver: AutomationTextResolver): List<String> = buildList {
        if (config.url.isBlank()) {
            add(resolver.resolve(R.string.automation_definition_action_open_website_error_missing_url))
        }
    }

    override fun summarize(config: OpenWebsiteActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_action_open_website_summary,
            listOf(
                config.url.ifBlank { resolver.resolve(R.string.automation_definition_action_open_website_unselected) },
            ),
        )

    private const val FIELD_URL = "url"
}
