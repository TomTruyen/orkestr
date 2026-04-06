package com.tomtruyen.orkestr.features.automation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
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
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
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
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.definition.TriggerDefinition
import com.tomtruyen.automation.generated.GeneratedActionProvider
import com.tomtruyen.automation.generated.GeneratedConstraintProvider
import com.tomtruyen.automation.generated.GeneratedTriggerProvider
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.ui.automation.R

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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(definition.titleRes),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        if (definition.isBeta) {
                            AssistChip(
                                onClick = {},
                                enabled = false,
                                label = { Text(stringResource(R.string.automation_label_beta)) },
                            )
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
