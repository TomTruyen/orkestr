package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.features.automation.component.AutomationCardColumn
import com.tomtruyen.orkestr.features.automation.component.AutomationSectionHeader
import com.tomtruyen.orkestr.features.automation.component.AutomationTitleRow
import com.tomtruyen.orkestr.features.automation.component.EmptyStateCard

@Composable
fun AutomationHomeScreen(
    rules: List<AutomationRule>,
    onCreateRule: () -> Unit,
    onEditRule: (AutomationRule) -> Unit,
    onDeleteRule: (AutomationRule) -> Unit,
    onToggleRuleEnabled: (AutomationRule, Boolean) -> Unit,
    summarizeTrigger: (com.tomtruyen.automation.features.triggers.config.TriggerConfig) -> String,
    summarizeConstraint: (com.tomtruyen.automation.features.constraints.config.ConstraintConfig) -> String,
    summarizeAction: (com.tomtruyen.automation.features.actions.config.ActionConfig) -> String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                AutomationCardColumn {
                    AutomationSectionHeader(
                        title = stringResource(R.string.automation_rules_intro_title),
                        description = stringResource(R.string.automation_rules_intro_description)
                    )
                    Button(onClick = onCreateRule) {
                        Text(stringResource(R.string.automation_action_create_rule))
                    }
                }
            }
        }

        if (rules.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_rules_title),
                    description = stringResource(R.string.automation_empty_rules_description)
                )
            }
        }

        itemsIndexed(rules, key = { _, rule -> rule.id }) { _, rule ->
            OutlinedCard {
                AutomationCardColumn {
                    AutomationTitleRow(
                        title = rule.name,
                        subtitle = stringResource(
                            R.string.automation_rule_counts,
                            rule.triggers.size,
                            rule.constraints.size,
                            rule.actions.size
                        ),
                        trailing = {
                            Switch(
                                checked = rule.enabled,
                                onCheckedChange = { onToggleRuleEnabled(rule, it) }
                            )
                        }
                    )
                    Text(
                        text = stringResource(
                            R.string.automation_rule_flow_summary,
                            rule.triggers.firstOrNull()?.let(summarizeTrigger)
                                ?: stringResource(R.string.automation_none_configured),
                            rule.actions.firstOrNull()?.let(summarizeAction)
                                ?: stringResource(R.string.automation_none_configured)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (rule.constraints.isNotEmpty()) {
                        Text(
                            text = summarizeConstraint(rule.constraints.first()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onEditRule(rule) }) {
                            Text(stringResource(R.string.automation_action_edit))
                        }
                        OutlinedButton(onClick = { onDeleteRule(rule) }) {
                            Text(stringResource(R.string.automation_action_delete))
                        }
                    }
                }
            }
        }
    }
}
