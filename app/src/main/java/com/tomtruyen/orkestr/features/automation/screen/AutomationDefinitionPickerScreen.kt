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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.features.automation.component.AutomationCardColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationFieldForm
import com.tomtruyen.orkestr.features.automation.component.AutomationSectionHeader
import com.tomtruyen.orkestr.features.automation.component.DefinitionFieldPreview
import com.tomtruyen.orkestr.features.automation.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.component.ValidationCard
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState

@Composable
fun AutomationDefinitionSelectionScreen(
    state: DefinitionPickerState,
    items: List<DefinitionListItem>,
    onQueryChanged: (String) -> Unit,
    onSelectDefinition: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.automation_search_label, stringResource(state.section.titleRes))) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.automation_search_placeholder,
                            stringResource(state.section.singularTitleRes).lowercase()
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
                    .clickable { onSelectDefinition(item.key) }
            ) {
                AutomationCardColumn {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DefinitionFieldPreview(fields = item.fields)
                }
            }
        }
    }
}

@Composable
fun AutomationDefinitionConfigurationScreen(
    state: DefinitionPickerState,
    definition: DefinitionListItem,
    onBackToList: () -> Unit,
    onFieldChanged: (String, String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        if (state.editingIndex == null) {
                            stringResource(
                                R.string.automation_action_add_node,
                                stringResource(state.section.singularTitleRes)
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
                OutlinedCard {
                    AutomationCardColumn {
                        AutomationSectionHeader(title = definition.title, description = definition.description)
                        Button(onClick = onBackToList) {
                            Text(
                                stringResource(
                                    R.string.automation_action_choose_different,
                                    stringResource(state.section.singularTitleRes)
                                )
                            )
                        }
                    }
                }
            }

            item {
                OutlinedCard {
                    AutomationCardColumn {
                        AutomationFieldForm(
                            fields = definition.fields,
                            values = state.values,
                            onFieldChanged = onFieldChanged
                        )
                        if (state.errors.isNotEmpty()) {
                            ValidationCard(errors = state.errors)
                        }
                    }
                }
            }
        }
    }
}
