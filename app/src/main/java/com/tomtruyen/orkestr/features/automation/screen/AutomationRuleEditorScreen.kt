package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.common.permission.AutomationPermissionManager
import com.tomtruyen.orkestr.features.automation.component.AutomationCardColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationTintedColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationTitleRow
import com.tomtruyen.orkestr.features.automation.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.component.NodeListItem
import com.tomtruyen.orkestr.features.automation.component.ValidationCard
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.ui.theme.ActionGreenContainer
import com.tomtruyen.orkestr.ui.theme.OnActionGreenContainer

@Composable
fun AutomationRuleEditorScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState.editorState ?: return
    val permissionManager = AutomationPermissionManager.remember(LocalContext.current)
    var nodeDialog by rememberSaveable { mutableStateOf<NodeActionDialogState?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Button(
                    onClick = { viewModel.onAction(AutomationEditorAction.SaveRuleClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.automation_action_save_rule))
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    AutomationCardColumn {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onAction(AutomationEditorAction.RuleNameChanged(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.automation_label_rule_name)) },
                            placeholder = { Text(stringResource(R.string.automation_rule_name_placeholder)) },
                            singleLine = true
                        )
                        AutomationTitleRow(
                            title = stringResource(R.string.automation_label_rule_enabled),
                            subtitle = stringResource(R.string.automation_rule_enabled_helper),
                            trailing = {
                                Switch(
                                    checked = state.enabled,
                                    onCheckedChange = {
                                        viewModel.onAction(AutomationEditorAction.RuleEnabledChanged(it))
                                    }
                                )
                            }
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
                    entries = state.triggers.map(viewModel::summarizeTrigger),
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onAddNode = { viewModel.onAction(AutomationEditorAction.AddNodeClicked(RuleSection.TRIGGERS)) },
                    onNodeClick = {
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.TRIGGERS, it)
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.TRIGGERS, it))
                        }
                    },
                    onNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(RuleSection.TRIGGERS, index, entry)
                    }
                )
            }
            item {
                RuleSectionEditorCard(
                    section = RuleSection.CONSTRAINTS,
                    entries = state.constraints.map(viewModel::summarizeConstraint),
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onAddNode = { viewModel.onAction(AutomationEditorAction.AddNodeClicked(RuleSection.CONSTRAINTS)) },
                    onNodeClick = {
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.CONSTRAINTS, it)
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.CONSTRAINTS, it))
                        }
                    },
                    onNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(RuleSection.CONSTRAINTS, index, entry)
                    }
                )
            }
            item {
                RuleSectionEditorCard(
                    section = RuleSection.ACTIONS,
                    entries = state.actions.map(viewModel::summarizeAction),
                    tint = ActionGreenContainer,
                    contentColor = OnActionGreenContainer,
                    onAddNode = { viewModel.onAction(AutomationEditorAction.AddNodeClicked(RuleSection.ACTIONS)) },
                    onNodeClick = {
                        permissionManager.request(
                            permissions = viewModel.requiredPermissionsForNode(RuleSection.ACTIONS, it)
                        ) {
                            viewModel.onAction(AutomationEditorAction.EditNodeClicked(RuleSection.ACTIONS, it))
                        }
                    },
                    onNodeLongClick = { index, entry ->
                        nodeDialog = NodeActionDialogState(RuleSection.ACTIONS, index, entry)
                    }
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
                    permissions = viewModel.requiredPermissionsForNode(dialog.section, dialog.index)
                ) {
                    viewModel.onAction(AutomationEditorAction.EditNodeClicked(dialog.section, dialog.index))
                }
                nodeDialog = null
            },
            onDelete = {
                viewModel.onAction(AutomationEditorAction.DeleteNodeClicked(dialog.section, dialog.index))
                nodeDialog = null
            }
        )
    }

    permissionManager.RenderDialogs()
}

private data class NodeActionDialogState(
    val section: RuleSection,
    val index: Int,
    val label: String
)

@Composable
private fun RuleSectionEditorCard(
    section: RuleSection,
    entries: List<String>,
    tint: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onAddNode: () -> Unit,
    onNodeClick: (Int) -> Unit,
    onNodeLongClick: (Int, String) -> Unit
) {
    AutomationTintedColumn(
        tint = tint,
        contentColor = contentColor
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 56.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = stringResource(section.titleRes), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(section.helperRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onAddNode, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(
                        R.string.automation_action_add_node,
                        stringResource(section.singularTitleRes)
                    )
                )
            }
        }

        if (entries.isEmpty()) {
            EmptyStateCard(
                title = stringResource(R.string.automation_empty_nodes_title),
                description = stringResource(
                    when (section) {
                        RuleSection.TRIGGERS -> R.string.automation_empty_triggers_description
                        RuleSection.CONSTRAINTS -> R.string.automation_empty_constraints_description
                        RuleSection.ACTIONS -> R.string.automation_empty_actions_description
                    }
                )
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                entries.forEachIndexed { index, entry ->
                    NodeListItem(
                        text = entry,
                        onClick = { onNodeClick(index) },
                        onLongClick = { onNodeLongClick(index, entry) }
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
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.automation_node_actions_title, state.label)) },
        text = { Text(stringResource(state.section.helperRes)) },
        confirmButton = {
            Button(onClick = onConfigure) {
                Text(stringResource(R.string.automation_action_configure))
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDelete) {
                    Text(stringResource(R.string.automation_action_delete))
                }
                OutlinedButton(onClick = onDismiss) {
                    Text(stringResource(R.string.automation_action_close))
                }
            }
        }
    )
}
