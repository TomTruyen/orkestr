package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.MusicActiveConstraintConfig

@GenerateConstraintDefinition
object MusicActiveConstraintDefinition : ConstraintDefinition<MusicActiveConstraintConfig>(
    configClass = MusicActiveConstraintConfig::class,
    defaultConfig = MusicActiveConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_music_active_title
    override val descriptionRes = R.string.automation_definition_constraint_music_active_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = MusicActiveConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "active",
            labelRes = R.string.automation_definition_constraint_music_active_field_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_music_active_field_description,
            defaultValue = true.toString(),
            reader = { it.active.toString() },
            updater = { config, value -> config.copy(active = value.toBoolean()) },
        ),
    )

    override fun summarize(config: MusicActiveConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_music_active_summary,
            listOf(resolver.resolve(config.active.activeInactiveRes())),
        )
}
