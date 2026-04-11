package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldDefinition
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.features.triggers.config.TimeZoneChangedTriggerConfig

@GenerateTriggerDefinition
object TimeZoneChangedTriggerDefinition : TriggerDefinition<TimeZoneChangedTriggerConfig>(
    configClass = TimeZoneChangedTriggerConfig::class,
    defaultConfig = TimeZoneChangedTriggerConfig,
) {
    override val titleRes = R.string.automation_definition_trigger_time_zone_changed_title
    override val descriptionRes = R.string.automation_definition_trigger_time_zone_changed_description
    override val fields = emptyList<AutomationFieldDefinition>()

    override fun summarize(config: TimeZoneChangedTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(R.string.automation_definition_trigger_time_zone_changed_summary)
}
