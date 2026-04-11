package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.R
import com.tomtruyen.automation.codegen.GenerateTriggerDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.definition.AutomationOption
import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.definition.TypedAutomationFieldDefinition
import com.tomtruyen.automation.features.triggers.config.NetworkConnectivityTriggerConfig

@GenerateTriggerDefinition
object NetworkConnectivityTriggerDefinition : TriggerDefinition<NetworkConnectivityTriggerConfig>(
    configClass = NetworkConnectivityTriggerConfig::class,
    defaultConfig = NetworkConnectivityTriggerConfig(),
) {
    override val titleRes = R.string.automation_definition_trigger_network_connectivity_title
    override val descriptionRes = R.string.automation_definition_trigger_network_connectivity_description
    override val fields = listOf(
        TypedAutomationFieldDefinition(
            configClass = NetworkConnectivityTriggerConfig::class,
            defaultConfig = defaultConfig,
            id = FIELD_CONNECTED,
            labelRes = R.string.automation_definition_trigger_connection_state_field_label,
            type = AutomationFieldType.SINGLE_CHOICE,
            descriptionRes = R.string.automation_definition_trigger_network_connectivity_field_description,
            defaultValue = VALUE_CONNECTED,
            options = connectionOptions(),
            reader = { it.connected.toConnectionFieldValue() },
            updater = { config, value -> config.copy(connected = value.toConnected()) },
        ),
    )

    override fun summarize(config: NetworkConnectivityTriggerConfig, resolver: AutomationTextResolver): String =
        resolver.resolve(
            R.string.automation_definition_trigger_network_connectivity_summary,
            listOf(resolver.resolve(config.connected.toConnectionLabelRes())),
        )
}

internal const val FIELD_CONNECTED = "connected"
internal const val VALUE_CONNECTED = "connected"
internal const val VALUE_DISCONNECTED = "disconnected"

internal fun connectionOptions(): List<AutomationOption> = listOf(
    AutomationOption(VALUE_CONNECTED, R.string.automation_definition_trigger_option_connected),
    AutomationOption(VALUE_DISCONNECTED, R.string.automation_definition_trigger_option_disconnected),
)

internal fun Boolean.toConnectionFieldValue(): String = if (this) VALUE_CONNECTED else VALUE_DISCONNECTED
internal fun String.toConnected(): Boolean = this != VALUE_DISCONNECTED
internal fun Boolean.toConnectionLabelRes(): Int = if (this) {
    R.string.automation_definition_trigger_option_connected
} else {
    R.string.automation_definition_trigger_option_disconnected
}
