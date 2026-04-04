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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.features.automation.component.AutomationCardColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationTintedColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationTitleRow
import com.tomtruyen.orkestr.features.automation.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.component.NodeListItem
import com.tomtruyen.orkestr.features.automation.component.ValidationCard
import com.tomtruyen.orkestr.features.automation.state.RuleEditorState
import com.tomtruyen.orkestr.features.automation.state.RuleSection

@Composable
fun AutomationRuleEditorScreen(
    state: RuleEditorState,
    onRuleNameChanged: (String) -> Unit,
    onRuleEnabledChanged: (Boolean) -> Unit,
    onSaveRule: () -> Unit,
    onOpenPicker: (RuleSection, Int?) -> Unit,
    onDeleteNode: (RuleSection, Int) -> Unit,
    summarizeTrigger: (com.tomtruyen.automation.features.triggers.config.TriggerConfig) -> String,
    summarizeConstraint: (com.tomtruyen.automation.features.constraints.config.ConstraintConfig) -> String,
    summarizeAction: (com.tomtruyen.automation.features.actions.config.ActionConfig) -> String,
    modifier: Modifier = Modifier
) {
    var nodeDialog by rememberSaveable { mutableStateOf<NodeActionDialogState?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Button(
                    onClick = onSaveRule,
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
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                    AutomationCardColumn {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onRuleNameChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.automation_label_rule_name)) },
                            placeholder = { Text(stringResource(R.string.automation_rule_name_placeholder)) },
                            singleLine = true
                        )
                        AutomationTitleRow(
                            title = stringResource(R.string.automation_label_rule_enabled),
                            subtitle = stringResource(R.string.automation_rule_enabled_helper),
                            trailing = {
                                Switch(checked = state.enabled, onCheckedChange = onRuleEnabledChanged)
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
                    entries = state.triggers.map(summarizeTrigger),
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    onAddNode = { onOpenPicker(RuleSection.TRIGGERS, null) },
                    onNodeClick = { onOpenPicker(RuleSection.TRIGGERS, it) },
                    onNodeLongClick = { index, entry -> nodeDialog = NodeActionDialogState(RuleSection.TRIGGERS, index, entry) }
                )
            }
            item {
                RuleSectionEditorCard(
                    section = RuleSection.CONSTRAINTS,
                    entries = state.constraints.map(summarizeConstraint),
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    onAddNode = { onOpenPicker(RuleSection.CONSTRAINTS, null) },
                    onNodeClick = { onOpenPicker(RuleSection.CONSTRAINTS, it) },
                    onNodeLongClick = { index, entry -> nodeDialog = NodeActionDialogState(RuleSection.CONSTRAINTS, index, entry) }
                )
            }
            item {
                RuleSectionEditorCard(
                    section = RuleSection.ACTIONS,
                    entries = state.actions.map(summarizeAction),
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    onAddNode = { onOpenPicker(RuleSection.ACTIONS, null) },
                    onNodeClick = { onOpenPicker(RuleSection.ACTIONS, it) },
                    onNodeLongClick = { index, entry -> nodeDialog = NodeActionDialogState(RuleSection.ACTIONS, index, entry) }
                )
            }
        }
    }

    nodeDialog?.let { dialog ->
        NodeActionDialog(
            state = dialog,
            onDismiss = { nodeDialog = null },
            onConfigure = {
                onOpenPicker(dialog.section, dialog.index)
                nodeDialog = null
            },
            onDelete = {
                onDeleteNode(dialog.section, dialog.index)
                nodeDialog = null
            }
        )
    }
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
    onAddNode: () -> Unit,
    onNodeClick: (Int) -> Unit,
    onNodeLongClick: (Int, String) -> Unit
) {
    AutomationTintedColumn(tint = tint) {
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
