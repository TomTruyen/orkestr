package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.core.model.PowerConnectionState
import com.tomtruyen.automation.features.triggers.config.PowerConnectionTriggerConfig

@GenerateTriggerDefinition
object PowerConnectionTriggerDefinition : TriggerDefinition<PowerConnectionTriggerConfig>(
    configClass = PowerConnectionTriggerConfig::class,
    defaultConfig = PowerConnectionTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_power_connection_title
    override val descriptionRes = R.string.automation_definition_trigger_power_connection_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = PowerConnectionTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_STATE,
            labelRes = R.string.automation_definition_trigger_power_connection_field_state_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_power_connection_field_state_description,
            defaultValue = VALUE_CONNECTED,
            options = listOf(
                AutomationOption(
                    VALUE_CONNECTED,
                    R.string.automation_definition_trigger_power_connection_option_connected,
                ),
                AutomationOption(
                    VALUE_DISCONNECTED,
                    R.string.automation_definition_trigger_power_connection_option_disconnected,
                ),
            ),
            reader = { it.state.toFieldValue() },
            updater = { config, value -> config.copy(state = value.toConnectionState()) },
        ),
    )

    override fun summarize(config: PowerConnectionTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_power_connection_summary,
            listOf(
                resolver.resolve(
                    when (config.state) {
                        PowerConnectionState.CONNECTED ->
                            R.string.automation_definition_trigger_power_connection_option_connected

                        PowerConnectionState.DISCONNECTED ->
                            R.string.automation_definition_trigger_power_connection_option_disconnected
                    },
                ),
            ),
        )

    private fun String.toConnectionState(): PowerConnectionState = when (this) {
        VALUE_DISCONNECTED -> PowerConnectionState.DISCONNECTED
        else -> PowerConnectionState.CONNECTED
    }

    private fun PowerConnectionState.toFieldValue(): String = when (this) {
        PowerConnectionState.CONNECTED -> VALUE_CONNECTED
        PowerConnectionState.DISCONNECTED -> VALUE_DISCONNECTED
    }

    private const val FIELD_STATE = "state"
    private const val VALUE_CONNECTED = "connected"
    private const val VALUE_DISCONNECTED = "disconnected"
}
