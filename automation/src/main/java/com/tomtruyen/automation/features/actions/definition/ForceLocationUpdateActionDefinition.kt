package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateActionDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldDefinition
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.features.actions.config.ForceLocationUpdateActionConfig

@GenerateActionDefinition
object ForceLocationUpdateActionDefinition : ActionDefinition<ForceLocationUpdateActionConfig>(
    configClass = ForceLocationUpdateActionConfig::class,
    defaultConfig = ForceLocationUpdateActionConfig(),
) {
    override val titleRes = R.string.automation_definition_action_force_location_update_title
    override val descriptionRes = R.string.automation_definition_action_force_location_update_description
    override val fields: List<AutomationFieldDefinition> = emptyList()

    override fun summarize(config: ForceLocationUpdateActionConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(R.string.automation_definition_action_force_location_update_summary)
}
