package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.common.permission.AutomationPermissionManager
import com.tomtruyen.orkestr.features.automation.component.AutomationCardColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationFieldForm
import com.tomtruyen.orkestr.features.automation.component.AutomationSectionHeader
import com.tomtruyen.orkestr.features.automation.component.DefinitionFieldPreview
import com.tomtruyen.orkestr.features.automation.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.component.ValidationCard
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel

@Composable
fun AutomationDefinitionSelectionScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState ?: return
    val items = viewModel.definitionItems(pickerState.section, pickerState.query)
    val permissionManager = AutomationPermissionManager.remember(LocalContext.current)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = pickerState.query,
                onValueChange = { viewModel.onAction(AutomationEditorAction.PickerQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.automation_search_label, stringResource(pickerState.section.titleRes))) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.automation_search_placeholder,
                            stringResource(pickerState.section.singularTitleRes).lowercase()
                        )
                    )
                },
                singleLine = true
            )
        }

        if (items.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_matches_title),
                    description = stringResource(R.string.automation_empty_matches_description)
                )
            }
        }

        items(items, key = { it.key }) { item ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        permissionManager.request(item.permissions) {
                            viewModel.onAction(AutomationEditorAction.DefinitionSelected(item.key))
                        }
                    }
            ) {
                AutomationCardColumn {
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(item.descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DefinitionFieldPreview(fields = item.fields)
                }
            }
        }
    }

    permissionManager.RenderDialogs()
}

@Composable
fun AutomationDefinitionConfigurationScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState ?: return
    pickerState.selectedTypeKey ?: return
    val definition = viewModel.selectedDefinitionItem() ?: return
    val fieldValues = viewModel.currentPickerFieldValues()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card {
                Button(
                    onClick = { viewModel.onAction(AutomationEditorAction.SavePickerClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        if (pickerState.editingIndex == null) {
                            stringResource(
                                R.string.automation_action_add_node,
                                stringResource(pickerState.section.singularTitleRes)
                            )
                        } else {
                            stringResource(R.string.automation_action_save_changes)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        AutomationSectionHeader(
                            title = stringResource(definition.titleRes),
                            description = stringResource(definition.descriptionRes)
                        )
                        if (pickerState.launchedFromSelection) {
                            Button(onClick = { viewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked) }) {
                                Text(
                                    stringResource(
                                        R.string.automation_action_choose_different,
                                        stringResource(pickerState.section.singularTitleRes)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        AutomationFieldForm(
                            fields = definition.fields,
                            values = fieldValues,
                            onFieldChanged = { fieldId, value ->
                                viewModel.onAction(AutomationEditorAction.PickerFieldChanged(fieldId, value))
                            }
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
