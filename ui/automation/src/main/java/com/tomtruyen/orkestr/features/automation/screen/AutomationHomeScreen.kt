package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationSectionHeader
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesAction
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import com.tomtruyen.orkestr.ui.automation.R
import com.tomtruyen.orkestr.ui.common.R as CommonR

@Composable
fun AutomationHomeScreen(viewModel: AutomationRulesViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    var pendingDelete by remember { mutableStateOf<AutomationRule?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                AutomationCardColumn {
                    AutomationSectionHeader(
                        title = stringResource(R.string.automation_rules_intro_title),
                        description = stringResource(R.string.automation_rules_intro_description),
                    )
                    Button(onClick = { viewModel.onAction(AutomationRulesAction.CreateRuleClicked) }) {
                        Text(stringResource(R.string.automation_action_create_rule))
                    }
                }
            }
        }

        if (state.rules.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_rules_title),
                    description = stringResource(R.string.automation_empty_rules_description),
                )
            }
        }

        itemsIndexed(state.rules, key = { _, rule -> rule.id }) { _, rule ->
            var menuExpanded by remember(rule.id) { mutableStateOf(false) }
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onAction(AutomationRulesAction.EditRuleClicked(rule)) },
            ) {
                AutomationCardColumn {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = rule.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row {
                                Switch(
                                    checked = rule.enabled,
                                    onCheckedChange = {
                                        viewModel.onAction(AutomationRulesAction.ToggleRuleEnabled(rule, it))
                                    },
                                )
                                Box {
                                    IconButton(onClick = { menuExpanded = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = stringResource(
                                                R.string.automation_action_more_options,
                                            ),
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
                                                viewModel.onAction(AutomationRulesAction.EditRuleClicked(rule))
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.automation_action_copy)) },
                                            onClick = {
                                                menuExpanded = false
                                                viewModel.onAction(AutomationRulesAction.CopyRuleClicked(rule))
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.automation_action_run_now)) },
                                            onClick = {
                                                menuExpanded = false
                                                viewModel.onAction(AutomationRulesAction.RunRuleNowClicked(rule))
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.automation_action_delete)) },
                                            onClick = {
                                                menuExpanded = false
                                                pendingDelete = rule
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.automation_rule_trigger_count, rule.triggers.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.automation_rule_constraint_count, rule.constraints.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.automation_rule_action_count, rule.actions.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { rule ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.automation_delete_rule_title)) },
            text = { Text(stringResource(R.string.automation_delete_rule_message, rule.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onAction(AutomationRulesAction.DeleteRuleClicked(rule))
                        pendingDelete = null
                    },
                ) {
                    Text(stringResource(R.string.automation_action_confirm_delete))
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
