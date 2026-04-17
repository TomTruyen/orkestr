package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationSectionHeader
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.component.DefinitionFieldPreview
import com.tomtruyen.orkestr.features.automation.state.AutomationGroupsAction
import com.tomtruyen.orkestr.features.automation.state.DefinitionCategoryGroup
import com.tomtruyen.orkestr.features.automation.state.nodeCount
import com.tomtruyen.orkestr.features.automation.state.withNodeAdded
import com.tomtruyen.orkestr.features.automation.state.withNodeRemovedAt
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationGroupsViewModel
import com.tomtruyen.orkestr.ui.automation.R
import com.tomtruyen.orkestr.ui.common.R as CommonR

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomationGroupsScreen(
    viewModel: AutomationGroupsViewModel,
    summarizeTrigger: (TriggerConfig) -> String,
    summarizeConstraint: (ConstraintConfig) -> String,
    summarizeAction: (ActionConfig) -> String,
    definitionCategoryGroups: (AutomationNodeGroupType, String) -> List<DefinitionCategoryGroup>,
    defaultNodeConfig: (AutomationNodeGroupType, String) -> AutomationConfig<*>?,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    AutomationGroupsContent(
        groups = state.groups,
        summarizeTrigger = summarizeTrigger,
        summarizeConstraint = summarizeConstraint,
        summarizeAction = summarizeAction,
        definitionCategoryGroups = definitionCategoryGroups,
        defaultNodeConfig = defaultNodeConfig,
        onDeleteGroup = { viewModel.onAction(AutomationGroupsAction.DeleteGroupClicked(it)) },
        onUpdateGroup = { viewModel.onAction(AutomationGroupsAction.UpdateGroupClicked(it)) },
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AutomationGroupsContent(
    groups: List<AutomationNodeGroup>,
    summarizeTrigger: (TriggerConfig) -> String,
    summarizeConstraint: (ConstraintConfig) -> String,
    summarizeAction: (ActionConfig) -> String,
    definitionCategoryGroups: (AutomationNodeGroupType, String) -> List<DefinitionCategoryGroup>,
    defaultNodeConfig: (AutomationNodeGroupType, String) -> AutomationConfig<*>?,
    onDeleteGroup: (AutomationNodeGroup) -> Unit,
    onUpdateGroup: (AutomationNodeGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedType by rememberSaveable { mutableStateOf(AutomationNodeGroupType.TRIGGER) }
    var pendingDelete by remember { mutableStateOf<AutomationNodeGroup?>(null) }
    var editingGroup by remember { mutableStateOf<AutomationNodeGroup?>(null) }
    val visibleGroups = groups.filter { it.type == selectedType }

    val currentEditingGroup = editingGroup
    if (currentEditingGroup != null) {
        AutomationGroupEditorPage(
            initialGroup = currentEditingGroup,
            summarizeTrigger = summarizeTrigger,
            summarizeConstraint = summarizeConstraint,
            summarizeAction = summarizeAction,
            definitionCategoryGroups = definitionCategoryGroups,
            defaultNodeConfig = defaultNodeConfig,
            onBack = { editingGroup = null },
            onSave = { updatedGroup ->
                onUpdateGroup(updatedGroup)
                editingGroup = null
            },
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AutomationSectionHeader(
                title = stringResource(R.string.automation_groups_title),
                description = stringResource(R.string.automation_groups_description),
            )
        }

        item {
            PrimaryTabRow(selectedTabIndex = selectedType.ordinal) {
                AutomationNodeGroupType.entries.forEach { type ->
                    Tab(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        text = { Text(stringResource(type.titleRes())) },
                    )
                }
            }
        }

        if (visibleGroups.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_groups_title),
                    description = stringResource(R.string.automation_empty_groups_description),
                )
            }
        }

        items(visibleGroups, key = { it.id }) { group ->
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                AutomationCardColumn {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.automation_group_node_count, group.nodeCount()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    group.summaries(
                        summarizeTrigger = summarizeTrigger,
                        summarizeConstraint = summarizeConstraint,
                        summarizeAction = summarizeAction,
                    ).take(MAX_GROUP_SUMMARIES).forEach { summary ->
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { editingGroup = group }) {
                            Text(stringResource(R.string.automation_action_edit))
                        }
                        OutlinedButton(onClick = { pendingDelete = group }) {
                            Text(stringResource(R.string.automation_action_delete))
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { group ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.automation_delete_group_title)) },
            text = { Text(stringResource(R.string.automation_delete_group_message, group.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteGroup(group)
                        pendingDelete = null
                    },
                ) {
                    Text(stringResource(R.string.automation_action_confirm_delete_group))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(CommonR.string.automation_action_close))
                }
            },
        )
    }
}

@Composable
internal fun AutomationGroupsScreenComposePreview() {
    AutomationGroupsContent(
        groups = listOf(
            AutomationNodeGroup(
                id = "triggers",
                name = "Charging at night",
                type = AutomationNodeGroupType.TRIGGER,
                triggers = listOf(BatteryChangedTriggerConfig()),
            ),
            AutomationNodeGroup(
                id = "constraints",
                name = "Low battery guard",
                type = AutomationNodeGroupType.CONSTRAINT,
                constraints = listOf(BatteryLevelConstraintConfig()),
            ),
            AutomationNodeGroup(
                id = "actions",
                name = "Debug notification",
                type = AutomationNodeGroupType.ACTION,
                actions = listOf(LogMessageActionConfig()),
            ),
        ),
        summarizeTrigger = { "When battery changes" },
        summarizeConstraint = { "Only if battery matches" },
        summarizeAction = { "Log a debug message" },
        definitionCategoryGroups = { _, _ -> emptyList() },
        defaultNodeConfig = { _, _ -> null },
        onDeleteGroup = {},
        onUpdateGroup = {},
    )
}

@Composable
private fun AutomationGroupEditorPage(
    initialGroup: AutomationNodeGroup,
    summarizeTrigger: (TriggerConfig) -> String,
    summarizeConstraint: (ConstraintConfig) -> String,
    summarizeAction: (ActionConfig) -> String,
    definitionCategoryGroups: (AutomationNodeGroupType, String) -> List<DefinitionCategoryGroup>,
    defaultNodeConfig: (AutomationNodeGroupType, String) -> AutomationConfig<*>?,
    onBack: () -> Unit,
    onSave: (AutomationNodeGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editedGroup by remember(initialGroup.id) { mutableStateOf(initialGroup) }
    var addQuery by rememberSaveable(initialGroup.id) { mutableStateOf("") }
    var expandedCategories by rememberSaveable(initialGroup.id) { mutableStateOf(setOf<String>()) }
    val definitionGroups = definitionCategoryGroups(editedGroup.type, addQuery)
    val summaries = editedGroup.summaries(
        summarizeTrigger = summarizeTrigger,
        summarizeConstraint = summarizeConstraint,
        summarizeAction = summarizeAction,
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AutomationSectionHeader(
                title = stringResource(R.string.automation_edit_group_title),
                description = stringResource(R.string.automation_edit_group_description),
            )
        }

        item {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                AutomationCardColumn {
                    OutlinedTextField(
                        value = editedGroup.name,
                        onValueChange = { editedGroup = editedGroup.copy(name = it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.automation_group_name_label)) },
                        singleLine = true,
                    )
                    Text(
                        text = stringResource(R.string.automation_group_node_count, editedGroup.nodeCount()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            GroupNodesCard(
                summaries = summaries,
                onRemoveNode = { index -> editedGroup = editedGroup.withNodeRemovedAt(index) },
            )
        }

        item {
            OutlinedTextField(
                value = addQuery,
                onValueChange = { addQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        stringResource(R.string.automation_search_label, stringResource(editedGroup.type.titleRes())),
                    )
                },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.automation_search_placeholder,
                            stringResource(editedGroup.type.singularTitleRes()).lowercase(),
                        ),
                    )
                },
                singleLine = true,
            )
        }

        if (definitionGroups.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_matches_title),
                    description = stringResource(R.string.automation_empty_matches_description),
                )
            }
        }

        definitionGroups.forEach { group ->
            item(key = "add-category-${group.category.name}") {
                DefinitionCategoryCard(
                    group = group,
                    expanded = group.category.name in expandedCategories,
                    onToggle = {
                        expandedCategories = if (group.category.name in expandedCategories) {
                            expandedCategories - group.category.name
                        } else {
                            expandedCategories + group.category.name
                        }
                    },
                    onDefinitionSelected = { typeKey ->
                        defaultNodeConfig(editedGroup.type, typeKey)?.let { config ->
                            editedGroup = editedGroup.withNodeAdded(config)
                        }
                    },
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text(stringResource(CommonR.string.automation_action_back))
                }
                Button(
                    onClick = { onSave(editedGroup) },
                    modifier = Modifier.weight(1f),
                    enabled = editedGroup.name.isNotBlank(),
                ) {
                    Text(stringResource(R.string.automation_action_save_changes))
                }
            }
        }
    }
}

@Composable
private fun GroupNodesCard(summaries: List<String>, onRemoveNode: (Int) -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        AutomationCardColumn {
            Text(
                text = stringResource(R.string.automation_group_nodes_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (summaries.isEmpty()) {
                Text(
                    text = stringResource(R.string.automation_empty_group_nodes_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                summaries.forEachIndexed { index, summary ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = summary,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        OutlinedButton(onClick = { onRemoveNode(index) }) {
                            Text(stringResource(R.string.automation_action_remove))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DefinitionCategoryCard(
    group: DefinitionCategoryGroup,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDefinitionSelected: (String) -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
            ) {
                Text(
                    text = if (expanded) "▾" else "▸",
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

            if (expanded) {
                group.items.forEachIndexed { index, item ->
                    if (index > 0) {
                        HorizontalDivider()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDefinitionSelected(item.key) }
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(item.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
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

private fun AutomationNodeGroup.summaries(
    summarizeTrigger: (TriggerConfig) -> String,
    summarizeConstraint: (ConstraintConfig) -> String,
    summarizeAction: (ActionConfig) -> String,
): List<String> = when (type) {
    AutomationNodeGroupType.TRIGGER -> triggers.map(summarizeTrigger)
    AutomationNodeGroupType.CONSTRAINT -> constraints.map(summarizeConstraint)
    AutomationNodeGroupType.ACTION -> actions.map(summarizeAction)
}

private fun AutomationNodeGroupType.titleRes(): Int = when (this) {
    AutomationNodeGroupType.TRIGGER -> R.string.automation_groups_triggers_title
    AutomationNodeGroupType.CONSTRAINT -> R.string.automation_groups_constraints_title
    AutomationNodeGroupType.ACTION -> R.string.automation_groups_actions_title
}

private fun AutomationNodeGroupType.singularTitleRes(): Int = when (this) {
    AutomationNodeGroupType.TRIGGER -> R.string.automation_singular_trigger
    AutomationNodeGroupType.CONSTRAINT -> R.string.automation_singular_constraint
    AutomationNodeGroupType.ACTION -> R.string.automation_singular_action
}

private const val MAX_GROUP_SUMMARIES = 5
