package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.common.permission.AutomationPermissionManager
import com.tomtruyen.orkestr.features.automation.component.AutomationBetaChip
import com.tomtruyen.orkestr.features.automation.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.features.automation.component.AutomationFieldForm
import com.tomtruyen.orkestr.features.automation.component.DefinitionFieldPreview
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.ui.automation.R

@Composable
fun AutomationDefinitionSelectionScreen(viewModel: AutomationRuleEditorViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState ?: return
    val groups = viewModel.definitionCategoryGroups(pickerState.section, pickerState.query)
    val permissionManager = AutomationPermissionManager.remember(LocalContext.current)
    var expandedCategories by rememberSaveable(pickerState.section) { mutableStateOf(setOf<String>()) }

    LaunchedEffect(pickerState.query, groups) {
        if (pickerState.query.isNotBlank()) {
            expandedCategories = expandedCategories + groups.map { it.category.name }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            OutlinedTextField(
                value = pickerState.query,
                onValueChange = { viewModel.onAction(AutomationEditorAction.PickerQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        stringResource(R.string.automation_search_label, stringResource(pickerState.section.titleRes)),
                    )
                },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.automation_search_placeholder,
                            stringResource(pickerState.section.singularTitleRes).lowercase(),
                        ),
                    )
                },
                singleLine = true,
            )
        }

        if (groups.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_matches_title),
                    description = stringResource(R.string.automation_empty_matches_description),
                )
            }
        }

        groups.forEach { group ->
            item(key = "category-${group.category.name}") {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedCategories = if (group.category.name in expandedCategories) {
                                expandedCategories - group.category.name
                            } else {
                                expandedCategories + group.category.name
                            }
                        },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                    ) {
                        Text(
                            text = if (group.category.name in expandedCategories) "▾" else "▸",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(group.category.titleRes),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = group.items.size.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (group.category.name in expandedCategories) {
                items(group.items, key = { it.key }) { item ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                permissionManager.request(item.permissions) {
                                    viewModel.onAction(AutomationEditorAction.DefinitionSelected(item.key))
                                }
                            },
                    ) {
                        AutomationCardColumn {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = stringResource(item.titleRes),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                if (item.isBeta) {
                                    AutomationBetaChip()
                                }
                            }
                            Text(
                                text = stringResource(item.descriptionRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            DefinitionFieldPreview(fields = item.fields)
                        }
                    }
                }
            }
        }
    }

    permissionManager.RenderDialogs()
}

@Composable
fun AutomationDefinitionConfigurationScreen(viewModel: AutomationRuleEditorViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState ?: return
    pickerState.selectedTypeKey ?: return
    val definition = viewModel.selectedDefinitionItem() ?: return
    val draftConfig = pickerState.draftConfig

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
                        AutomationFieldForm(
                            fields = definition.fields,
                            config = draftConfig,
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
