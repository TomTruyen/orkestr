package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.features.automation.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.features.automation.component.TimeBasedTriggerConfigurationForm
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.ui.automation.R

@Composable
fun AutomationTimeBasedTriggerConfigurationScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState ?: return
    val definition = viewModel.selectedDefinitionItem() ?: return

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Card {
                Button(
                    onClick = { viewModel.onAction(AutomationEditorAction.SavePickerClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        if (pickerState.editingIndex == null) {
                            stringResource(
                                R.string.automation_action_add_node,
                                stringResource(pickerState.section.singularTitleRes),
                            )
                        } else {
                            stringResource(R.string.automation_action_save_changes)
                        },
                    )
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
                    chooseDifferentLabel = if (pickerState.launchedFromSelection) {
                        stringResource(
                            R.string.automation_action_choose_different,
                            stringResource(pickerState.section.singularTitleRes),
                        )
                    } else {
                        null
                    },
                    onChooseDifferent = if (pickerState.launchedFromSelection) {
                        { viewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked) }
                    } else {
                        null
                    },
                )
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        TimeBasedTriggerConfigurationForm(
                            config = viewModel.currentTimeBasedTriggerConfig(),
                            onFieldChanged = { fieldId, value ->
                                viewModel.onAction(AutomationEditorAction.PickerFieldChanged(fieldId, value))
                            },
                        )
                        if (pickerState.errors.isNotEmpty()) {
                            ValidationCard(errors = pickerState.errors)
                        }
                    }
                }
            }
        }
    }
}
