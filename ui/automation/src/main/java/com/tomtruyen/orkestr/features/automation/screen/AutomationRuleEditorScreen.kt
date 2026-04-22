package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationTintedColumn
import com.tomtruyen.orkestr.common.component.AutomationTitleRow
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.common.permission.AutomationPermissionManager
import com.tomtruyen.orkestr.features.automation.component.ActionExecutionModeSelector
import com.tomtruyen.orkestr.features.automation.component.NodeListItem
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.ui.automation.R
import com.tomtruyen.orkestr.ui.common.R as CommonR

@Composable
fun AutomationRuleEditorScreen(viewModel: AutomationRuleEditorViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState.editorState ?: return
    val permissionManager = AutomationPermissionManager.remember(LocalContext.current)
    var nodeDialog by rememberSaveable { mutableStateOf<NodeActionDialogState?>(null) }
    var groupDialogState by rememberSaveable { mutableStateOf<SaveGroupDialogState?>(null) }
    var selectionState by rememberSaveable { mutableStateOf<NodeSelectionState?>(null) }
    val constraintLayout = conditionGroupLayout(
        constraints = state.constraints,
        conditionGroups = state.constraintGroups.map { it.constraints },
        summarize = viewModel::summarizeConstraint,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Button(
                    onClick = { viewModel.onAction(AutomationEditorAction.SaveRuleClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(stringResource(R.string.automation_action_save_rule))
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    AutomationCardColumn {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onAction(AutomationEditorAction.RuleNameChanged(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.automation_label_rule_name)) },
                            placeholder = { Text(stringResource(R.string.automation_rule_name_placeholder)) },
                            singleLine = true,
                        )
                        AutomationTitleRow(
                            title = stringResource(R.string.automation_label_rule_enabled),
                            subtitle = stringResource(R.string.automation_rule_enabled_helper),
                            trailing = {
                                Switch(
                                    checked = state.enabled,
                                    onCheckedChange = {
                                        viewModel.onAction(AutomationEditorAction.RuleEnabledChanged(it))
                                    },
                                )
                            },
                        )
                        if (state.validation.errors.isNotEmpty()) {
                            ValidationCard(errors = state.validation.errors)
                        }
                    }
                }
            }

            item {
                RuleSectionEditorCard(
                    section = RuleSection.TRIGGERS,
                    entries = state.triggers.mapIndexed { index, trigger ->
                        NodeEntry(index = index, label = viewModel.summarizeTrigger(trigger))
                    },
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onAddNode = { viewModel.onAction(AutomationEditorAction.AddNodeClicked(RuleSection.TRIGGERS)) },
                    onNodeClick = {
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.TRIGGERS, it),
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.TRIGGERS, it))
                        }
                    },
                    onNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(RuleSection.TRIGGERS, index, entry)
                    },
                    onSaveGroup = { groupDialogState = SaveGroupDialogState(RuleSection.TRIGGERS) },
                )
            }
            item {
                RuleSectionEditorCard(
                    section = RuleSection.CONSTRAINTS,
                    entries = constraintLayout.ungroupedEntries,
                    conditionGroups = constraintLayout.conditionGroups,
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onAddNode = { viewModel.onAction(AutomationEditorAction.AddNodeClicked(RuleSection.CONSTRAINTS)) },
                    onNodeClick = {
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.CONSTRAINTS, it),
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.CONSTRAINTS, it))
                        }
                    },
                    onNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(RuleSection.CONSTRAINTS, index, entry)
                    },
                    onSaveGroup = { groupDialogState = SaveGroupDialogState(RuleSection.CONSTRAINTS) },
                    onAddConstraintToConditionGroup = { groupIndex ->
                        viewModel.onAction(AutomationEditorAction.AddConstraintToConditionGroupClicked(groupIndex))
                    },
                    onConditionNodeClick = { index ->
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.CONSTRAINTS, index),
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.CONSTRAINTS, index))
                        }
                    },
                    onConditionNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(
                            section = RuleSection.CONSTRAINTS,
                            index = index,
                            label = entry,
                            conditionGroupIndex = constraintLayout.groupIndexForConstraint(index),
                        )
                    },
                    onEditConditionGroup = { group ->
                        selectionState = NodeSelectionState(
                            section = RuleSection.CONSTRAINTS,
                            mode = NodeSelectionMode.EDIT_CONDITION_GROUP,
                            selectedIndices = group.entries.map { it.index }.toSet(),
                            conditionGroupIndex = group.index,
                        )
                    },
                    onCopyConditionGroup = { groupIndex ->
                        viewModel.onAction(AutomationEditorAction.CopyConstraintConditionGroupClicked(groupIndex))
                    },
                    onDeleteConditionGroup = { groupIndex ->
                        viewModel.onAction(AutomationEditorAction.DeleteConstraintConditionGroupClicked(groupIndex))
                    },
                )
            }
            item {
                RuleSectionEditorCard(
                    section = RuleSection.ACTIONS,
                    entries = state.actions.mapIndexed { index, action ->
                        NodeEntry(index = index, label = viewModel.summarizeAction(action))
                    },
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onAddNode = { viewModel.onAction(AutomationEditorAction.AddNodeClicked(RuleSection.ACTIONS)) },
                    headerContent = {
                        ActionExecutionModeSelector(
                            selectedMode = state.actionExecutionMode,
                            onModeSelected = {
                                viewModel.onAction(AutomationEditorAction.RuleActionExecutionModeChanged(it))
                            },
                        )
                    },
                    onNodeClick = {
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.ACTIONS, it),
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.ACTIONS, it))
                        }
                    },
                    onNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(RuleSection.ACTIONS, index, entry)
                    },
                    onSaveGroup = { groupDialogState = SaveGroupDialogState(RuleSection.ACTIONS) },
                )
            }
        }
    }

    nodeDialog?.let { dialog ->
        NodeActionDialog(
            state = dialog,
            onDismiss = { nodeDialog = null },
            onConfigure = {
                permissionManager.request(
                    permissions = viewModel.requiredPermissionsForNode(dialog.section, dialog.index),
                ) {
                    viewModel.onAction(AutomationEditorAction.EditNodeClicked(dialog.section, dialog.index))
                }
                nodeDialog = null
            },
            onDelete = {
                viewModel.onAction(AutomationEditorAction.DeleteNodeClicked(dialog.section, dialog.index))
                nodeDialog = null
            },
            onSelectTemplateNodes = {
                selectionState = NodeSelectionState(
                    section = dialog.section,
                    mode = NodeSelectionMode.TEMPLATE_GROUP,
                    selectedIndices = setOf(dialog.index),
                )
                nodeDialog = null
            },
            onSelectConditionNodes = {
                selectionState = NodeSelectionState(
                    section = dialog.section,
                    mode = NodeSelectionMode.CONDITION_GROUP,
                    selectedIndices = setOf(dialog.index),
                )
                nodeDialog = null
            },
            onRemoveFromConditionGroup = dialog.conditionGroupIndex?.let { groupIndex ->
                {
                    viewModel.onAction(
                        AutomationEditorAction.RemoveConstraintFromConditionGroupClicked(
                            groupIndex = groupIndex,
                            constraintIndex = dialog.index,
                        ),
                    )
                    nodeDialog = null
                }
            },
        )
    }

    selectionState?.let { selection ->
        NodeSelectionBottomSheet(
            selection = selection,
            entries = selection.entries(
                triggerEntries = state.triggers.mapIndexed { index, trigger ->
                    NodeEntry(index = index, label = viewModel.summarizeTrigger(trigger))
                },
                constraintEntries = state.constraints.mapIndexed { index, constraint ->
                    NodeEntry(index = index, label = viewModel.summarizeConstraint(constraint))
                },
                actionEntries = state.actions.mapIndexed { index, action ->
                    NodeEntry(index = index, label = viewModel.summarizeAction(action))
                },
            ),
            onSelectionChanged = { selectionState = it },
            onDismiss = { selectionState = null },
            onConfirm = {
                when (selection.mode) {
                    NodeSelectionMode.TEMPLATE_GROUP ->
                        groupDialogState = SaveGroupDialogState(selection.section, selection.selectedIndices)

                    NodeSelectionMode.CONDITION_GROUP ->
                        viewModel.onAction(
                            AutomationEditorAction.CreateConstraintConditionGroupClicked(selection.selectedIndices),
                        )

                    NodeSelectionMode.EDIT_CONDITION_GROUP ->
                        selection.conditionGroupIndex?.let { groupIndex ->
                            viewModel.onAction(
                                AutomationEditorAction.UpdateConstraintConditionGroupClicked(
                                    groupIndex = groupIndex,
                                    indices = selection.selectedIndices,
                                ),
                            )
                        }
                }
                selectionState = null
            },
        )
    }

    groupDialogState?.let { dialogState ->
        SaveGroupDialog(
            visible = true,
            sectionName = stringResource(dialogState.section.singularTitleRes),
            onDismiss = { groupDialogState = null },
            onSave = { name ->
                if (dialogState.indices == null) {
                    viewModel.onAction(AutomationEditorAction.SaveSectionAsGroupClicked(dialogState.section, name))
                } else {
                    viewModel.onAction(
                        AutomationEditorAction.SaveSelectedNodesAsGroupClicked(
                            section = dialogState.section,
                            indices = dialogState.indices,
                            name = name,
                        ),
                    )
                    selectionState = null
                }
                groupDialogState = null
            },
        )
    }

    permissionManager.RenderDialogs()
}

private data class NodeActionDialogState(
    val section: RuleSection,
    val index: Int,
    val label: String,
    val conditionGroupIndex: Int? = null,
)
private data class SaveGroupDialogState(val section: RuleSection, val indices: Set<Int>? = null)
private data class NodeSelectionState(
    val section: RuleSection,
    val mode: NodeSelectionMode,
    val selectedIndices: Set<Int> = emptySet(),
    val conditionGroupIndex: Int? = null,
)
private enum class NodeSelectionMode {
    TEMPLATE_GROUP,
    CONDITION_GROUP,
    EDIT_CONDITION_GROUP,
}
private data class NodeEntry(val index: Int, val label: String)
private data class ConstraintLayoutState(
    val ungroupedEntries: List<NodeEntry>,
    val conditionGroups: List<ConditionGroupUiState>,
) {
    fun groupIndexForConstraint(constraintIndex: Int): Int? =
        conditionGroups.firstOrNull { group -> group.entries.any { it.index == constraintIndex } }?.index
}
private data class ConditionGroupUiState(val index: Int, val entries: List<NodeEntry>)

private fun conditionGroupLayout(
    constraints: List<ConstraintConfig>,
    conditionGroups: List<List<ConstraintConfig>>,
    summarize: (ConstraintConfig) -> String,
): ConstraintLayoutState {
    val groupedIndices = mutableSetOf<Int>()
    val groupStates = conditionGroups.mapIndexedNotNull { groupIndex, groupConstraints ->
        val groupIndices = mutableSetOf<Int>()
        val entries = groupConstraints.mapNotNull { groupedConstraint ->
            val constraintIndex = constraints.indices.firstOrNull { index ->
                index !in groupIndices && constraints[index] == groupedConstraint
            } ?: return@mapNotNull null

            groupIndices += constraintIndex
            groupedIndices += constraintIndex
            NodeEntry(index = constraintIndex, label = summarize(groupedConstraint))
        }

        entries.takeIf { it.isNotEmpty() }?.let {
            ConditionGroupUiState(index = groupIndex, entries = it)
        }
    }

    val ungroupedEntries = constraints.mapIndexedNotNull { index, constraint ->
        if (index in groupedIndices) {
            null
        } else {
            NodeEntry(index = index, label = summarize(constraint))
        }
    }

    return ConstraintLayoutState(
        ungroupedEntries = ungroupedEntries,
        conditionGroups = groupStates,
    )
}

@Composable
private fun RuleSectionEditorCard(
    section: RuleSection,
    entries: List<NodeEntry>,
    conditionGroups: List<ConditionGroupUiState> = emptyList(),
    tint: Color,
    contentColor: Color,
    onAddNode: () -> Unit,
    headerContent: @Composable (() -> Unit)? = null,
    onNodeClick: (Int) -> Unit,
    onNodeLongClick: (Int, String) -> Unit,
    onSaveGroup: () -> Unit,
    onAddConstraintToConditionGroup: (Int) -> Unit = {},
    onConditionNodeClick: (Int) -> Unit = {},
    onConditionNodeLongClick: (Int, String) -> Unit = { _, _ -> },
    onEditConditionGroup: (ConditionGroupUiState) -> Unit = {},
    onCopyConditionGroup: (Int) -> Unit = {},
    onDeleteConditionGroup: (Int) -> Unit = {},
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    AutomationTintedColumn(
        tint = tint,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(section.titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = onAddNode) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(
                            R.string.automation_action_add_node,
                            stringResource(section.singularTitleRes),
                        ),
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.automation_action_more_options),
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.automation_action_save_as_group)) },
                            enabled = entries.isNotEmpty() || conditionGroups.isNotEmpty(),
                            onClick = {
                                menuExpanded = false
                                onSaveGroup()
                            },
                        )
                    }
                }
            }

            Text(
                text = stringResource(section.helperRes),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f),
            )
        }

        headerContent?.invoke()

        if (conditionGroups.isNotEmpty()) {
            ConditionGroupsSummary(
                conditionGroups = conditionGroups,
                onAddConstraintToConditionGroup = onAddConstraintToConditionGroup,
                onConditionNodeClick = onConditionNodeClick,
                onConditionNodeLongClick = onConditionNodeLongClick,
                onEditConditionGroup = onEditConditionGroup,
                onCopyConditionGroup = onCopyConditionGroup,
                onDeleteConditionGroup = onDeleteConditionGroup,
            )
        }

        if (entries.isEmpty() && conditionGroups.isEmpty()) {
            EmptyStateCard(
                title = stringResource(R.string.automation_empty_nodes_title),
                description = stringResource(
                    when (section) {
                        RuleSection.TRIGGERS -> R.string.automation_empty_triggers_description
                        RuleSection.CONSTRAINTS -> R.string.automation_empty_constraints_description
                        RuleSection.ACTIONS -> R.string.automation_empty_actions_description
                    },
                ),
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                entries.forEach { entry ->
                    NodeListItem(
                        text = entry.label,
                        onClick = { onNodeClick(entry.index) },
                        onLongClick = { onNodeLongClick(entry.index, entry.label) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NodeActionDialog(
    state: NodeActionDialogState,
    onDismiss: () -> Unit,
    onConfigure: () -> Unit,
    onDelete: () -> Unit,
    onSelectTemplateNodes: () -> Unit,
    onSelectConditionNodes: () -> Unit,
    onRemoveFromConditionGroup: (() -> Unit)?,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.automation_node_actions_title, state.label)) },
        text = {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    DialogActionListItem(
                        title = stringResource(R.string.automation_action_configure),
                        description = stringResource(R.string.automation_action_configure_node_description),
                        onClick = onConfigure,
                    )
                    HorizontalDivider()
                    DialogActionListItem(
                        title = stringResource(R.string.automation_action_select_nodes_for_group),
                        description = stringResource(R.string.automation_action_select_nodes_for_group_description),
                        onClick = onSelectTemplateNodes,
                    )
                    if (state.section == RuleSection.CONSTRAINTS) {
                        HorizontalDivider()
                        if (onRemoveFromConditionGroup == null) {
                            DialogActionListItem(
                                title = stringResource(
                                    R.string.automation_action_select_constraints_for_condition_group,
                                ),
                                description = stringResource(
                                    R.string.automation_action_select_constraints_for_condition_group_description,
                                ),
                                onClick = onSelectConditionNodes,
                            )
                        } else {
                            DialogActionListItem(
                                title = stringResource(R.string.automation_action_remove_from_condition_group),
                                description = stringResource(
                                    R.string.automation_action_remove_from_condition_group_description,
                                ),
                                onClick = onRemoveFromConditionGroup,
                            )
                        }
                    }
                    HorizontalDivider()
                    DialogActionListItem(
                        title = stringResource(R.string.automation_action_delete),
                        description = stringResource(R.string.automation_action_delete_node_description),
                        onClick = onDelete,
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(CommonR.string.automation_action_close))
            }
        },
    )
}

@Composable
private fun DialogActionListItem(title: String, description: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text = title, style = MaterialTheme.typography.titleSmall) },
        supportingContent = { Text(text = description, style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NodeSelectionBottomSheet(
    selection: NodeSelectionState,
    entries: List<NodeEntry>,
    onSelectionChanged: (NodeSelectionState) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val selectedCount = selection.selectedIndices.size
    ModalBottomSheet(onDismissRequest = onDismiss) {
        AutomationCardColumn {
            Text(
                text = stringResource(selection.titleRes),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.automation_group_selection_count, selectedCount),
                style = MaterialTheme.typography.bodyMedium,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                entries.forEach { entry ->
                    val selected = entry.index in selection.selectedIndices
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectionChanged(selection.withNodeSelectionToggled(entry.index))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = {
                                onSelectionChanged(selection.withNodeSelectionToggled(entry.index))
                            },
                        )
                        Text(
                            text = entry.label,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            }
            Button(
                enabled = selectedCount > 0,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(selection.confirmRes))
            }
        }
    }
}

private val NodeSelectionState.titleRes: Int
    get() = when (mode) {
        NodeSelectionMode.TEMPLATE_GROUP -> R.string.automation_selection_template_group_title
        NodeSelectionMode.CONDITION_GROUP -> R.string.automation_selection_condition_group_title
        NodeSelectionMode.EDIT_CONDITION_GROUP -> R.string.automation_selection_edit_condition_group_title
    }

private val NodeSelectionState.confirmRes: Int
    get() = when (mode) {
        NodeSelectionMode.TEMPLATE_GROUP -> R.string.automation_action_continue
        NodeSelectionMode.CONDITION_GROUP -> R.string.automation_action_create_condition_group
        NodeSelectionMode.EDIT_CONDITION_GROUP -> R.string.automation_action_save_changes
    }

@Composable
private fun ConditionGroupsSummary(
    conditionGroups: List<ConditionGroupUiState>,
    onAddConstraintToConditionGroup: (Int) -> Unit,
    onConditionNodeClick: (Int) -> Unit,
    onConditionNodeLongClick: (Int, String) -> Unit,
    onEditConditionGroup: (ConditionGroupUiState) -> Unit,
    onCopyConditionGroup: (Int) -> Unit,
    onDeleteConditionGroup: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.automation_conditions_title),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.automation_conditions_helper),
            style = MaterialTheme.typography.bodySmall,
        )
        conditionGroups.forEachIndexed { index, group ->
            if (index > 0) {
                Text(
                    text = stringResource(R.string.automation_condition_or_separator),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                var menuExpanded by rememberSaveable(group.index) { mutableStateOf(false) }
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.automation_condition_group_label),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { onAddConstraintToConditionGroup(group.index) }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(
                                    R.string.automation_action_add_constraint_to_condition_group,
                                ),
                            )
                        }
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(R.string.automation_action_more_options),
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.automation_action_edit)) },
                                    onClick = {
                                        menuExpanded = false
                                        onEditConditionGroup(group)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.automation_action_copy)) },
                                    onClick = {
                                        menuExpanded = false
                                        onCopyConditionGroup(group.index)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.automation_action_delete)) },
                                    onClick = {
                                        menuExpanded = false
                                        onDeleteConditionGroup(group.index)
                                    },
                                )
                            }
                        }
                    }
                    group.entries.forEachIndexed { summaryIndex, entry ->
                        if (summaryIndex > 0) {
                            Text(
                                text = stringResource(R.string.automation_condition_and_separator),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        }
                        NodeListItem(
                            text = entry.label,
                            onClick = { onConditionNodeClick(entry.index) },
                            onLongClick = { onConditionNodeLongClick(entry.index, entry.label) },
                        )
                    }
                }
            }
        }
    }
}

private fun NodeSelectionState.withNodeSelectionToggled(index: Int): NodeSelectionState = copy(
    selectedIndices = if (index in selectedIndices) {
        selectedIndices - index
    } else {
        selectedIndices + index
    },
)

private fun NodeSelectionState.entries(
    triggerEntries: List<NodeEntry>,
    constraintEntries: List<NodeEntry>,
    actionEntries: List<NodeEntry>,
): List<NodeEntry> = when (section) {
    RuleSection.TRIGGERS -> triggerEntries
    RuleSection.CONSTRAINTS -> constraintEntries
    RuleSection.ACTIONS -> actionEntries
}

@Preview(showBackground = true, widthDp = 420)
@Composable
@Suppress("MagicNumber")
internal fun AutomationRuleEditorConditionSelectionPreview() {
    MaterialTheme {
        RuleSectionEditorCard(
            section = RuleSection.CONSTRAINTS,
            entries = listOf(
                NodeEntry(0, "Only if battery at least 80%"),
                NodeEntry(1, "Only if Wi-Fi is on"),
                NodeEntry(2, "Only if headphones are connected"),
            ),
            conditionGroups = listOf(
                ConditionGroupUiState(
                    0,
                    listOf(
                        NodeEntry(3, "Only if Bluetooth is on"),
                        NodeEntry(4, "Only if power is connected"),
                    ),
                ),
                ConditionGroupUiState(1, listOf(NodeEntry(5, "Only if location is inside Home"))),
            ),
            tint = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            onAddNode = {},
            onNodeClick = {},
            onNodeLongClick = { _, _ -> },
            onSaveGroup = {},
        )
    }
}
