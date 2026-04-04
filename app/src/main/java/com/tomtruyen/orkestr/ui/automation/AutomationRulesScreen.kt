package com.tomtruyen.orkestr.ui.automation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.definition.ActionDefinition
import com.tomtruyen.automation.data.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.data.definition.AutomationFieldDefinition
import com.tomtruyen.automation.data.definition.AutomationFieldType
import com.tomtruyen.automation.data.definition.ConstraintDefinition
import com.tomtruyen.automation.data.definition.TriggerDefinition

@Composable
fun AutomationRulesRoute(viewModel: AutomationRulesViewModel) {
    val rules by viewModel.rules.collectAsState()
    AutomationRulesScreen(
        rules = rules,
        destination = viewModel.destination,
        editorState = viewModel.editorState,
        nodeEditorState = viewModel.nodeEditorState,
        definitions = viewModel.definitions,
        onOpenRules = viewModel::openRules,
        onOpenCatalog = viewModel::openCatalog,
        onCreateRule = viewModel::createRule,
        onEditRule = viewModel::editRule,
        onDeleteRule = viewModel::deleteRule,
        onToggleRuleEnabled = viewModel::toggleRuleEnabled,
        onCancelEditing = viewModel::cancelEditing,
        onRuleNameChanged = viewModel::updateRuleName,
        onRuleEnabledChanged = viewModel::updateRuleEnabled,
        onSaveRule = viewModel::saveRule,
        onOpenNodeEditor = viewModel::openNodeEditor,
        onCloseNodeEditor = viewModel::closeNodeEditor,
        onNodeTypeChanged = viewModel::updateNodeType,
        onNodeFieldChanged = viewModel::updateNodeField,
        onCommitNode = viewModel::commitNode,
        onDeleteNode = viewModel::deleteNode,
        summarizeTrigger = viewModel::summarizeTrigger,
        summarizeConstraint = viewModel::summarizeConstraint,
        summarizeAction = viewModel::summarizeAction
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AutomationRulesScreen(
    rules: List<AutomationRule>,
    destination: AutomationDestination,
    editorState: RuleEditorState?,
    nodeEditorState: NodeEditorState?,
    definitions: AutomationDefinitionRegistry,
    onOpenRules: () -> Unit,
    onOpenCatalog: () -> Unit,
    onCreateRule: () -> Unit,
    onEditRule: (AutomationRule) -> Unit,
    onDeleteRule: (AutomationRule) -> Unit,
    onToggleRuleEnabled: (AutomationRule, Boolean) -> Unit,
    onCancelEditing: () -> Unit,
    onRuleNameChanged: (String) -> Unit,
    onRuleEnabledChanged: (Boolean) -> Unit,
    onSaveRule: () -> Unit,
    onOpenNodeEditor: (RuleSection, Int?) -> Unit,
    onCloseNodeEditor: () -> Unit,
    onNodeTypeChanged: (String) -> Unit,
    onNodeFieldChanged: (String, String) -> Unit,
    onCommitNode: () -> Unit,
    onDeleteNode: (RuleSection, Int) -> Unit,
    summarizeTrigger: (com.tomtruyen.automation.features.triggers.config.TriggerConfig) -> String,
    summarizeConstraint: (com.tomtruyen.automation.features.constraints.config.ConstraintConfig) -> String,
    summarizeAction: (com.tomtruyen.automation.features.actions.config.ActionConfig) -> String
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            when (destination) {
                                AutomationDestination.RULES -> "Automation Rules"
                                AutomationDestination.CATALOG -> "Catalog"
                                AutomationDestination.EDITOR -> "Rule Editor"
                            }
                        )
                    },
                    actions = {
                        if (destination == AutomationDestination.EDITOR) {
                            TextButton(onClick = onCancelEditing) {
                                Text("Back")
                            }
                        } else {
                            TextButton(onClick = onCreateRule) {
                                Text("New Rule")
                            }
                        }
                    }
                )
                if (destination != AutomationDestination.EDITOR) {
                    TabRow(selectedTabIndex = if (destination == AutomationDestination.RULES) 0 else 1) {
                        Tab(
                            selected = destination == AutomationDestination.RULES,
                            onClick = onOpenRules,
                            text = { Text("Rules") }
                        )
                        Tab(
                            selected = destination == AutomationDestination.CATALOG,
                            onClick = onOpenCatalog,
                            text = { Text("Definitions") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (destination) {
            AutomationDestination.RULES -> RulesListScreen(
                rules = rules,
                onCreateRule = onCreateRule,
                onEditRule = onEditRule,
                onDeleteRule = onDeleteRule,
                onToggleRuleEnabled = onToggleRuleEnabled,
                summarizeTrigger = summarizeTrigger,
                summarizeConstraint = summarizeConstraint,
                summarizeAction = summarizeAction,
                modifier = Modifier.padding(innerPadding)
            )

            AutomationDestination.CATALOG -> DefinitionsCatalogScreen(
                definitions = definitions,
                modifier = Modifier.padding(innerPadding)
            )

            AutomationDestination.EDITOR -> editorState?.let {
                RuleEditorScreen(
                    state = it,
                    onRuleNameChanged = onRuleNameChanged,
                    onRuleEnabledChanged = onRuleEnabledChanged,
                    onSaveRule = onSaveRule,
                    onOpenNodeEditor = onOpenNodeEditor,
                    onDeleteNode = onDeleteNode,
                    summarizeTrigger = summarizeTrigger,
                    summarizeConstraint = summarizeConstraint,
                    summarizeAction = summarizeAction,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    if (nodeEditorState != null) {
        NodeEditorDialog(
            state = nodeEditorState,
            definitions = definitions,
            onDismiss = onCloseNodeEditor,
            onTypeChanged = onNodeTypeChanged,
            onFieldChanged = onNodeFieldChanged,
            onConfirm = onCommitNode
        )
    }
}

@Composable
private fun RulesListScreen(
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
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Build clear automations",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Each rule shows exactly what starts it, which constraints apply, and what happens next.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onCreateRule) {
                        Text("Create Rule")
                    }
                }
            }
        }

        if (rules.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No rules yet",
                    description = "Create your first automation rule to define triggers, optional constraints, and actions."
                )
            }
        }

        itemsIndexed(rules, key = { _, rule -> rule.id }) { _, rule ->
            OutlinedCard(shape = RoundedCornerShape(24.dp)) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = rule.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${rule.triggers.size} triggers • ${rule.constraints.size} constraints • ${rule.actions.size} actions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = rule.enabled,
                            onCheckedChange = { onToggleRuleEnabled(rule, it) }
                        )
                    }

                    FlowSummaryCard(
                        title = "When",
                        entries = rule.triggers.map(summarizeTrigger),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                    FlowSummaryCard(
                        title = "Only If",
                        entries = rule.constraints.map(summarizeConstraint),
                        tint = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    FlowSummaryCard(
                        title = "Then",
                        entries = rule.actions.map(summarizeAction),
                        tint = MaterialTheme.colorScheme.secondaryContainer
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onEditRule(rule) }) {
                            Text("Edit")
                        }
                        OutlinedButton(onClick = { onDeleteRule(rule) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleEditorScreen(
    state: RuleEditorState,
    onRuleNameChanged: (String) -> Unit,
    onRuleEnabledChanged: (Boolean) -> Unit,
    onSaveRule: () -> Unit,
    onOpenNodeEditor: (RuleSection, Int?) -> Unit,
    onDeleteNode: (RuleSection, Int) -> Unit,
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
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onRuleNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Rule name") },
                        placeholder = { Text("Pause notifications while charging") },
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rule enabled",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Disabled rules stay in the database but will not run.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = state.enabled, onCheckedChange = onRuleEnabledChanged)
                    }
                    if (state.validation.errors.isNotEmpty()) {
                        ValidationCard(errors = state.validation.errors)
                    }
                    Button(onClick = onSaveRule) {
                        Text("Save Rule")
                    }
                }
            }
        }

        item {
            EditorSectionCard(
                title = RuleSection.TRIGGERS.title,
                helper = RuleSection.TRIGGERS.helper,
                entries = state.triggers.map(summarizeTrigger),
                section = RuleSection.TRIGGERS,
                onOpenNodeEditor = onOpenNodeEditor,
                onDeleteNode = onDeleteNode
            )
        }

        item {
            EditorSectionCard(
                title = RuleSection.CONSTRAINTS.title,
                helper = RuleSection.CONSTRAINTS.helper,
                entries = state.constraints.map(summarizeConstraint),
                section = RuleSection.CONSTRAINTS,
                onOpenNodeEditor = onOpenNodeEditor,
                onDeleteNode = onDeleteNode
            )
        }

        item {
            EditorSectionCard(
                title = RuleSection.ACTIONS.title,
                helper = RuleSection.ACTIONS.helper,
                entries = state.actions.map(summarizeAction),
                section = RuleSection.ACTIONS,
                onOpenNodeEditor = onOpenNodeEditor,
                onDeleteNode = onDeleteNode
            )
        }
    }
}

@Composable
private fun EditorSectionCard(
    title: String,
    helper: String,
    entries: List<String>,
    section: RuleSection,
    onOpenNodeEditor: (RuleSection, Int?) -> Unit,
    onDeleteNode: (RuleSection, Int) -> Unit
) {
    OutlinedCard(shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(text = helper, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (entries.isEmpty()) {
                EmptyStateCard(
                    title = "Nothing added",
                    description = when (section) {
                        RuleSection.TRIGGERS -> "At least one trigger is required."
                        RuleSection.CONSTRAINTS -> "Constraints are optional."
                        RuleSection.ACTIONS -> "At least one action is required."
                    }
                )
            } else {
                entries.forEachIndexed { index, entry ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = entry, style = MaterialTheme.typography.bodyLarge)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { onOpenNodeEditor(section, index) }) {
                                    Text("Edit")
                                }
                                OutlinedButton(onClick = { onDeleteNode(section, index) }) {
                                    Text("Remove")
                                }
                            }
                        }
                    }
                }
            }
            Button(onClick = { onOpenNodeEditor(section, null) }) {
                Text("Add ${section.title}")
            }
        }
    }
}

@Composable
private fun DefinitionsCatalogScreen(
    definitions: AutomationDefinitionRegistry,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CatalogGroupCard(
                title = "Triggers",
                description = "Triggers start a rule.",
                definitions = definitions.triggers
            )
        }
        item {
            CatalogGroupCard(
                title = "Constraints",
                description = "Constraints refine when the rule can continue.",
                definitions = definitions.constraints
            )
        }
        item {
            CatalogGroupCard(
                title = "Actions",
                description = "Actions are executed after the rule validates.",
                definitions = definitions.actions
            )
        }
    }
}

@Composable
private fun CatalogGroupCard(
    title: String,
    description: String,
    definitions: List<Any>
) {
    OutlinedCard(shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            definitions.forEachIndexed { index, definition ->
                when (definition) {
                    is TriggerDefinition -> DefinitionCard(definition.title, definition.description, definition.fields)
                    is ConstraintDefinition -> DefinitionCard(definition.title, definition.description, definition.fields)
                    is ActionDefinition -> DefinitionCard(definition.title, definition.description, definition.fields)
                }
                if (index != definitions.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun DefinitionCard(
    title: String,
    description: String,
    fields: List<AutomationFieldDefinition>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
        fields.forEach { field ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = field.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(
                        text = field.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Field type: ${field.type.name.lowercase().replace('_', ' ')}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun NodeEditorDialog(
    state: NodeEditorState,
    definitions: AutomationDefinitionRegistry,
    onDismiss: () -> Unit,
    onTypeChanged: (String) -> Unit,
    onFieldChanged: (String, String) -> Unit,
    onConfirm: () -> Unit
) {
    val description = when (state.section) {
        RuleSection.TRIGGERS -> definitions.trigger(
            com.tomtruyen.automation.features.triggers.TriggerType.valueOf(state.selectedTypeKey)
        )?.description
        RuleSection.CONSTRAINTS -> definitions.constraint(
            com.tomtruyen.automation.features.constraints.ConstraintType.valueOf(state.selectedTypeKey)
        )?.description
        RuleSection.ACTIONS -> definitions.action(
            com.tomtruyen.automation.features.actions.ActionType.valueOf(state.selectedTypeKey)
        )?.description
    } ?: return

    val fields = when (state.section) {
        RuleSection.TRIGGERS -> definitions.trigger(
            com.tomtruyen.automation.features.triggers.TriggerType.valueOf(state.selectedTypeKey)
        )?.fields
        RuleSection.CONSTRAINTS -> definitions.constraint(
            com.tomtruyen.automation.features.constraints.ConstraintType.valueOf(state.selectedTypeKey)
        )?.fields
        RuleSection.ACTIONS -> definitions.action(
            com.tomtruyen.automation.features.actions.ActionType.valueOf(state.selectedTypeKey)
        )?.fields
    } ?: return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (state.editingIndex == null) "Add ${state.section.title}" else "Edit ${state.section.title}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = when (state.section) {
                        RuleSection.TRIGGERS -> "Choose the trigger type and configure its settings."
                        RuleSection.CONSTRAINTS -> "Choose the optional constraint and configure how it filters the rule."
                        RuleSection.ACTIONS -> "Choose the action type and configure what should happen."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                DefinitionTypePicker(
                    section = state.section,
                    selectedTypeKey = state.selectedTypeKey,
                    definitions = definitions,
                    onTypeChanged = onTypeChanged
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                fields.forEach { field ->
                    AutomationFieldEditor(
                        field = field,
                        value = state.values[field.id].orEmpty(),
                        onValueChanged = { onFieldChanged(field.id, it) }
                    )
                }

                if (state.errors.isNotEmpty()) {
                    ValidationCard(errors = state.errors)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DefinitionTypePicker(
    section: RuleSection,
    selectedTypeKey: String,
    definitions: AutomationDefinitionRegistry,
    onTypeChanged: (String) -> Unit
) {
    val types = when (section) {
        RuleSection.TRIGGERS -> definitions.triggers.map { it.type.name to it.title }
        RuleSection.CONSTRAINTS -> definitions.constraints.map { it.type.name to it.title }
        RuleSection.ACTIONS -> definitions.actions.map { it.type.name to it.title }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Type", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { (key, title) ->
                FilterChip(
                    selected = selectedTypeKey == key,
                    onClick = { onTypeChanged(key) },
                    label = { Text(title) }
                )
            }
        }
    }
}

@Composable
private fun AutomationFieldEditor(
    field: AutomationFieldDefinition,
    value: String,
    onValueChanged: (String) -> Unit
) {
    when (field.type) {
        AutomationFieldType.TEXT,
        AutomationFieldType.NUMBER -> OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(field.label) },
            placeholder = { if (field.placeholder.isNotBlank()) Text(field.placeholder) },
            supportingText = { Text(field.description) },
            singleLine = true
        )

        AutomationFieldType.BOOLEAN -> Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(field.label, style = MaterialTheme.typography.titleSmall)
                    Text(field.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = value == "true",
                    onCheckedChange = { onValueChanged(it.toString()) }
                )
            }
        }

        AutomationFieldType.SINGLE_CHOICE -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(field.label, style = MaterialTheme.typography.labelLarge)
            Text(field.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                field.options.forEach { option ->
                    FilterChip(
                        selected = option.value == value,
                        onClick = { onValueChanged(option.value) },
                        label = { Text(option.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlowSummaryCard(
    title: String,
    entries: List<String>,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = tint
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            if (entries.isEmpty()) {
                Text("None", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                entries.forEach { entry ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(8.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = entry,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationCard(errors: List<String>) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Validation",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.SemiBold
            )
            errors.distinct().forEach { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
