package com.tomtruyen.orkestr.features.automation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.timebased.screen.TimeBasedTriggerConfigurationScreen
import com.tomtruyen.orkestr.features.wifi.screen.WifiTriggerSelectionScreen
import com.tomtruyen.orkestr.ui.automation.R

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
        config = editorViewModel.currentTimeBasedTriggerConfig(),
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

    WifiTriggerSelectionScreen(
        currentConfig = editorViewModel.currentWifiTriggerConfig(),
        title = wifiHeaderTitle(pickerState, definition),
        description = wifiHeaderDescription(pickerState, definition),
        isBeta = definition?.isBeta == true,
        requiredMinSdk = definition?.requiredMinSdk,
        chooseDifferentLabel = pickerState.chooseDifferentLabel(),
        onChooseDifferent = pickerState.chooseDifferentAction(editorViewModel),
        onWifiSelected = editorViewModel::applySelectedWifiTrigger,
        modifier = modifier,
    )
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
