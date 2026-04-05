package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig

@GenerateTriggerDefinition
object GeofenceTriggerDefinition : TriggerDefinition<GeofenceTriggerConfig>(
    configClass = GeofenceTriggerConfig::class,
    defaultConfig = GeofenceTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_geofence_title
    override val descriptionRes = R.string.automation_definition_trigger_geofence_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = GeofenceTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_TRANSITION,
            labelRes = R.string.automation_definition_trigger_geofence_field_transition_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_geofence_field_transition_description,
            defaultValue = VALUE_ENTER,
            options = listOf(
                AutomationOption(
                    VALUE_ENTER,
                    R.string.automation_definition_trigger_geofence_option_enter,
                ),
                AutomationOption(
                    VALUE_EXIT,
                    R.string.automation_definition_trigger_geofence_option_exit,
                ),
            ),
            reader = { it.transitionType.toFieldValue() },
            updater = { config, value -> config.copy(transitionType = value.toTransitionType()) },
        ),
        TypedAutomationFieldDefinition(
            configClass = GeofenceTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_UPDATE_RATE,
            labelRes = R.string.automation_definition_trigger_geofence_field_update_rate_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_geofence_field_update_rate_description,
            defaultValue = VALUE_UPDATE_RATE_BALANCED,
            options = listOf(
                AutomationOption(
                    VALUE_UPDATE_RATE_FAST,
                    R.string.automation_definition_trigger_geofence_option_update_rate_fast,
                ),
                AutomationOption(
                    VALUE_UPDATE_RATE_BALANCED,
                    R.string.automation_definition_trigger_geofence_option_update_rate_balanced,
                ),
                AutomationOption(
                    VALUE_UPDATE_RATE_RELAXED,
                    R.string.automation_definition_trigger_geofence_option_update_rate_relaxed,
                ),
            ),
            reader = { it.updateRate.toFieldValue() },
            updater = { config, value -> config.copy(updateRate = value.toUpdateRate()) },
        ),
    )

    override fun validate(config: GeofenceTriggerConfig, resolver: AutomationTextResolver): List<String> {
        val errors = mutableListOf<String>()
        if (config.geofenceId.isBlank()) {
            errors += resolver.resolve(R.string.automation_definition_trigger_geofence_error_missing_geofence)
        }
        return errors
    }

    override fun summarize(config: GeofenceTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_geofence_summary,
            listOf(
                resolver.resolve(
                    when (config.transitionType) {
                        GeofenceTransitionType.ENTER ->
                            R.string.automation_definition_trigger_geofence_transition_enter

                        GeofenceTransitionType.EXIT ->
                            R.string.automation_definition_trigger_geofence_transition_exit
                    },
                ),
                config.geofenceName.ifBlank {
                    resolver.resolve(R.string.automation_definition_trigger_geofence_unselected)
                },
                resolver.resolve(config.updateRate.labelRes),
            ),
        )

    private fun String.toTransitionType(): GeofenceTransitionType = when (this) {
        VALUE_EXIT -> GeofenceTransitionType.EXIT
        else -> GeofenceTransitionType.ENTER
    }

    private fun GeofenceTransitionType.toFieldValue(): String = when (this) {
        GeofenceTransitionType.ENTER -> VALUE_ENTER
        GeofenceTransitionType.EXIT -> VALUE_EXIT
    }

    private fun String.toUpdateRate(): GeofenceUpdateRate = when (this) {
        VALUE_UPDATE_RATE_FAST -> GeofenceUpdateRate.FAST
        VALUE_UPDATE_RATE_RELAXED -> GeofenceUpdateRate.RELAXED
        else -> GeofenceUpdateRate.BALANCED
    }

    private fun GeofenceUpdateRate.toFieldValue(): String = when (this) {
        GeofenceUpdateRate.FAST -> VALUE_UPDATE_RATE_FAST
        GeofenceUpdateRate.BALANCED -> VALUE_UPDATE_RATE_BALANCED
        GeofenceUpdateRate.RELAXED -> VALUE_UPDATE_RATE_RELAXED
    }

    private val GeofenceUpdateRate.labelRes: Int
        get() = when (this) {
            GeofenceUpdateRate.FAST -> R.string.automation_definition_trigger_geofence_option_update_rate_fast
            GeofenceUpdateRate.BALANCED -> R.string.automation_definition_trigger_geofence_option_update_rate_balanced
            GeofenceUpdateRate.RELAXED -> R.string.automation_definition_trigger_geofence_option_update_rate_relaxed
        }

    private const val FIELD_TRANSITION = "transition"
    private const val FIELD_UPDATE_RATE = "update_rate"
    private const val VALUE_ENTER = "enter"
    private const val VALUE_EXIT = "exit"
    private const val VALUE_UPDATE_RATE_FAST = "fast"
    private const val VALUE_UPDATE_RATE_BALANCED = "balanced"
    private const val VALUE_UPDATE_RATE_RELAXED = "relaxed"
}
