package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateConstraintDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig

@GenerateConstraintDefinition
object GeofenceConstraintDefinition : ConstraintDefinition<GeofenceConstraintConfig>(
    configClass = GeofenceConstraintConfig::class,
    defaultConfig = GeofenceConstraintConfig(),
) {
    override val titleRes = R.string.automation_definition_constraint_geofence_title
    override val descriptionRes = R.string.automation_definition_constraint_geofence_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = GeofenceConstraintConfig::class,
            defaultConfig = defaultConfig,
            id = "inside",
            labelRes = R.string.automation_definition_constraint_geofence_state_label,
            type = AutomationFieldType.BOOLEAN,
            descriptionRes = R.string.automation_definition_constraint_geofence_state_description,
            defaultValue = true.toString(),
            reader = { it.inside.toString() },
            updater = { config, value -> config.copy(inside = value.toBoolean()) },
        ),
    )

    override fun validate(config: GeofenceConstraintConfig, resolver: AutomationTextResolver): List<String> {
        val errors = super.validate(config, resolver).toMutableList()
        if (config.geofenceId.isBlank()) {
            errors += resolver.resolve(R.string.automation_definition_constraint_geofence_error_missing_geofence)
        }
        return errors
    }

    override fun summarize(config: GeofenceConstraintConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_constraint_geofence_summary,
            listOf(
                resolver.resolve(config.inside.insideOutsideRes()),
                config.geofenceName.ifBlank {
                    resolver.resolve(R.string.automation_definition_constraint_geofence_unselected)
                },
            ),
        )
}
