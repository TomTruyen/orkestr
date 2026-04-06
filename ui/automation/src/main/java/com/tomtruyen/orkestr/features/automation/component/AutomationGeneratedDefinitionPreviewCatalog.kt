package com.tomtruyen.orkestr.features.automation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
import com.tomtruyen.automation.core.model.PowerConnectionState
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.DoNotDisturbActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import com.tomtruyen.automation.features.actions.definition.ActionDefinition
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.constraints.definition.ConstraintDefinition
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatteryLevelTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatterySaverStateTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.PowerConnectionTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.automation.features.triggers.definition.TriggerDefinition
import com.tomtruyen.automation.generated.GeneratedActionProvider
import com.tomtruyen.automation.generated.GeneratedConstraintProvider
import com.tomtruyen.automation.generated.GeneratedTriggerProvider
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationBetaChip
import com.tomtruyen.orkestr.common.component.AutomationRequiredSdkChip

internal object AutomationGeneratedDefinitionPreviewCatalog {
    val triggerTypes: Set<TriggerType> = GeneratedTriggerProvider.definitions.mapTo(linkedSetOf()) { it.type }
    val constraintTypes: Set<ConstraintType> = GeneratedConstraintProvider.definitions.mapTo(linkedSetOf()) { it.type }
    val actionTypes: Set<ActionType> = GeneratedActionProvider.definitions.mapTo(linkedSetOf()) { it.type }

    fun trigger(type: TriggerType): TriggerDefinition<*> =
        GeneratedTriggerProvider.definitions.first { it.type == type }

    fun constraint(type: ConstraintType): ConstraintDefinition<*> =
        GeneratedConstraintProvider.definitions.first { it.type == type }

    fun action(type: ActionType): ActionDefinition<*> = GeneratedActionProvider.definitions.first { it.type == type }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ChargeStateTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.CHARGE_STATE),
        config = BatteryChangedTriggerConfig(state = BatteryChargeState.FULL),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun TimeBasedTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.TIME_BASED),
        config = TimeBasedTriggerConfig(
            hour = 8,
            minute = 30,
            days = setOf(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY),
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatteryLevelTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.BATTERY_LEVEL),
        config = BatteryLevelTriggerConfig(
            operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
            value = 20,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun PowerConnectionTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.POWER_CONNECTION),
        config = PowerConnectionTriggerConfig(state = PowerConnectionState.DISCONNECTED),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatterySaverStateTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.BATTERY_SAVER_STATE),
        config = BatterySaverStateTriggerConfig(enabled = true),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ApplicationLifecycleTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.APPLICATION_LIFECYCLE),
        config = ApplicationLifecycleTriggerConfig(
            packageName = "com.spotify.music",
            transitionType = AppLifecycleTransitionType.CLOSED,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun NotificationReceivedTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.NOTIFICATION_RECEIVED),
        config = NotificationReceivedTriggerConfig(packageName = "com.whatsapp"),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun WifiSsidTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.WIFI_SSID_IN_RANGE),
        config = WifiSsidTriggerConfig(ssid = "Office WiFi"),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun GeofenceTriggerDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.trigger(TriggerType.GEOFENCE),
        config = GeofenceTriggerConfig(
            geofenceId = "home",
            geofenceName = "Home",
            transitionType = GeofenceTransitionType.EXIT,
            updateRate = GeofenceUpdateRate.FAST,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun BatteryLevelConstraintDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.constraint(ConstraintType.BATTERY_LEVEL),
        config = BatteryLevelConstraintConfig(
            operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
            value = 20,
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun DoNotDisturbActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.DO_NOT_DISTURB),
        config = DoNotDisturbActionConfig(mode = DoNotDisturbMode.ALARMS_ONLY),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ShowNotificationActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.SHOW_NOTIFICATION),
        config = ShowNotificationActionConfig(
            title = "Battery Saver",
            message = "Power saving mode has been enabled.",
        ),
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun LogMessageActionDefinitionComposePreview() {
    AutomationDefinitionPreviewCard(
        definition = AutomationGeneratedDefinitionPreviewCatalog.action(ActionType.LOG_MESSAGE),
        config = LogMessageActionConfig(message = "Logged from automation preview coverage."),
    )
}

@Composable
private fun AutomationDefinitionPreviewCard(definition: AutomationNodeDefinition<*, *>, config: AutomationConfig<*>?) {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                AutomationCardColumn {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(definition.titleRes),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            if (definition.isBeta) {
                                AutomationBetaChip()
                            }
                            definition.requiredMinSdk?.let { requiredMinSdk ->
                                AutomationRequiredSdkChip(requiredMinSdk = requiredMinSdk)
                            }
                        }
                    }
                    Text(
                        text = stringResource(definition.descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AutomationFieldForm(
                        fields = definition.fields,
                        config = config,
                        onFieldChanged = { _, _ -> },
                    )
                }
            }
        }
    }
}
