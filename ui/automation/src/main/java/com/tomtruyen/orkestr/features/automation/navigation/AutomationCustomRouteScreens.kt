package com.tomtruyen.orkestr.features.automation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.timebased.component.AutomationTimePickerDialog
import com.tomtruyen.orkestr.features.timebased.screen.TimeBasedTriggerConfigurationScreen
import com.tomtruyen.orkestr.features.wifi.screen.WifiTriggerSelectionScreen
import com.tomtruyen.orkestr.ui.automation.R
import com.tomtruyen.automation.R as AutomationR

@Composable
internal fun TimeBasedTriggerRouteScreen(
    editorViewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val pickerState = editorViewModel.uiState.collectAsState().value.pickerState ?: return
    val definition = editorViewModel.selectedDefinitionItem() ?: return

    TimeBasedTriggerConfigurationScreen(
        title = stringResource(definition.titleRes),
        description = stringResource(definition.descriptionRes),
        isBeta = definition.isBeta,
        requiredMinSdk = definition.requiredMinSdk,
        chooseDifferentLabel = pickerState.chooseDifferentLabel(),
        saveLabel = pickerState.saveLabel(),
        errors = pickerState.errors,
        config = editorViewModel.currentDraftConfigOrDefault(TimeBasedTriggerConfig::class),
        onFieldChanged = { fieldId, value ->
            editorViewModel.onAction(AutomationEditorAction.PickerFieldChanged(fieldId, value))
        },
        onSave = {
            editorViewModel.onAction(AutomationEditorAction.SavePickerClicked)
        },
        onChooseDifferent = pickerState.chooseDifferentAction(editorViewModel),
        modifier = modifier,
    )
}

@Composable
internal fun WifiTriggerRouteScreen(editorViewModel: AutomationRuleEditorViewModel, modifier: Modifier = Modifier) {
    val pickerState = editorViewModel.uiState.collectAsState().value.pickerState
    val definition = editorViewModel.selectedDefinitionItem()
    val currentConfig = if (pickerState?.section == RuleSection.CONSTRAINTS) {
        val constraintConfig = editorViewModel.currentDraftConfigOrDefault(WifiSsidConstraintConfig::class)
        WifiSsidTriggerConfig(ssid = constraintConfig.ssid)
    } else {
        editorViewModel.currentDraftConfigOrDefault(WifiSsidTriggerConfig::class)
    }

    WifiTriggerSelectionScreen(
        currentConfig = currentConfig,
        title = wifiHeaderTitle(pickerState, definition),
        description = wifiHeaderDescription(pickerState, definition),
        isBeta = definition?.isBeta == true,
        requiredMinSdk = definition?.requiredMinSdk,
        chooseDifferentLabel = pickerState.chooseDifferentLabel(),
        onChooseDifferent = pickerState.chooseDifferentAction(editorViewModel),
        showTriggerTypeSelector = pickerState?.section != RuleSection.CONSTRAINTS,
        onWifiSelected = editorViewModel::applySelectedWifi,
        modifier = modifier,
    )
}

@Composable
internal fun TimeOfDayConstraintRouteScreen(
    editorViewModel: AutomationRuleEditorViewModel,
    config: TimeOfDayConstraintConfig,
    modifier: Modifier = Modifier,
) {
    val pickerState = editorViewModel.uiState.collectAsState().value.pickerState ?: return
    val definition = editorViewModel.selectedDefinitionItem() ?: return
    var activePicker by remember { mutableStateOf<TimePickerTarget?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Card {
                Button(
                    onClick = { editorViewModel.onAction(AutomationEditorAction.SavePickerClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(pickerState.saveLabel())
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AutomationDefinitionHeaderCard(
                    title = stringResource(definition.titleRes),
                    description = stringResource(definition.descriptionRes),
                    isBeta = definition.isBeta,
                    requiredMinSdk = definition.requiredMinSdk,
                    chooseDifferentLabel = pickerState.chooseDifferentLabel(),
                    onChooseDifferent = pickerState.chooseDifferentAction(editorViewModel),
                )
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        TimeSelectionCard(
                            label = stringResource(
                                AutomationR.string.automation_definition_constraint_time_of_day_start_label,
                            ),
                            description = stringResource(
                                AutomationR.string.automation_definition_constraint_time_of_day_start_description,
                            ),
                            value = config.startTime.format24Hour(),
                            onClick = { activePicker = TimePickerTarget.START },
                        )
                        TimeSelectionCard(
                            label = stringResource(
                                AutomationR.string.automation_definition_constraint_time_of_day_end_label,
                            ),
                            description = stringResource(
                                AutomationR.string.automation_definition_constraint_time_of_day_end_description,
                            ),
                            value = config.endTime.format24Hour(),
                            onClick = { activePicker = TimePickerTarget.END },
                        )
                        if (pickerState.errors.isNotEmpty()) {
                            ValidationCard(errors = pickerState.errors)
                        }
                    }
                }
            }
        }
    }

    val pickerTarget = activePicker
    if (pickerTarget != null) {
        val time = when (pickerTarget) {
            TimePickerTarget.START -> config.startTime
            TimePickerTarget.END -> config.endTime
        }
        AutomationTimePickerDialog(
            hour = time.hour,
            minute = time.minute,
            onDismiss = { activePicker = null },
            onConfirm = { hour, minute ->
                val fieldId = when (pickerTarget) {
                    TimePickerTarget.START -> "startTime"
                    TimePickerTarget.END -> "endTime"
                }
                editorViewModel.onAction(
                    AutomationEditorAction.PickerFieldChanged(
                        fieldId = fieldId,
                        value = "%02d:%02d".format(hour, minute),
                    ),
                )
                activePicker = null
            },
        )
    }
}

@Composable
private fun DefinitionPickerState?.chooseDifferentLabel(): String? {
    if (this == null) return null
    return stringResource(
        R.string.automation_action_choose_different,
        stringResource(section.singularTitleRes),
    )
}

@Composable
private fun DefinitionPickerState.saveLabel(): String = if (editingIndex == null) {
    stringResource(
        R.string.automation_action_add_node,
        stringResource(section.singularTitleRes),
    )
} else {
    stringResource(R.string.automation_action_save_changes)
}

private fun DefinitionPickerState?.chooseDifferentAction(
    editorViewModel: AutomationRuleEditorViewModel,
): (() -> Unit)? = if (this == null) null else editorViewModel::chooseDifferentDefinition

@Composable
private fun wifiHeaderTitle(pickerState: DefinitionPickerState?, definition: DefinitionListItem?): String? {
    if (pickerState?.launchedFromSelection != true) {
        return null
    }
    return definition?.let { stringResource(it.titleRes) }
        ?: stringResource(R.string.automation_title_select_wifi_network)
}

@Composable
private fun wifiHeaderDescription(pickerState: DefinitionPickerState?, definition: DefinitionListItem?): String? {
    if (pickerState?.launchedFromSelection != true) {
        return null
    }
    return definition?.let { stringResource(it.descriptionRes) }
        ?: stringResource(R.string.automation_title_select_wifi_network)
}

private enum class TimePickerTarget {
    START,
    END,
}

@Composable
private fun TimeSelectionCard(label: String, description: String, value: String, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        AutomationCardColumn {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                )
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
