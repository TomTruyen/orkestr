package com.tomtruyen.orkestr.ui.automation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.data.definition.ActionDefinition
import com.tomtruyen.automation.data.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.data.definition.ConstraintDefinition
import com.tomtruyen.automation.data.definition.TriggerDefinition
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AutomationDestination {
    RULES,
    CATALOG,
    EDITOR
}

enum class RuleSection(val title: String, val helper: String) {
    TRIGGERS("When", "Add one or more triggers that start the rule."),
    CONSTRAINTS("Only If", "Constraints refine when the rule is allowed to continue."),
    ACTIONS("Then", "Add one or more actions that should run.")
}

data class RuleValidationState(
    val errors: List<String> = emptyList()
)

data class RuleEditorState(
    val id: String,
    val name: String = "",
    val enabled: Boolean = true,
    val triggers: List<TriggerConfig> = emptyList(),
    val constraints: List<ConstraintConfig> = emptyList(),
    val actions: List<ActionConfig> = emptyList(),
    val validation: RuleValidationState = RuleValidationState()
)

data class NodeEditorState(
    val section: RuleSection,
    val editingIndex: Int?,
    val selectedTypeKey: String,
    val values: Map<String, String>,
    val errors: List<String> = emptyList()
)

class AutomationRulesViewModel(
    private val repository: AutomationRuleRepository,
    val definitions: AutomationDefinitionRegistry
) : ViewModel() {
    val rules: StateFlow<List<AutomationRule>> = repository.observeRules().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    var destination by mutableStateOf(AutomationDestination.RULES)
        private set

    var editorState by mutableStateOf<RuleEditorState?>(null)
        private set

    var nodeEditorState by mutableStateOf<NodeEditorState?>(null)
        private set

    fun openRules() {
        destination = AutomationDestination.RULES
        nodeEditorState = null
    }

    fun openCatalog() {
        destination = AutomationDestination.CATALOG
        nodeEditorState = null
    }

    fun createRule() {
        editorState = RuleEditorState(id = UUID.randomUUID().toString())
        destination = AutomationDestination.EDITOR
    }

    fun editRule(rule: AutomationRule) {
        editorState = RuleEditorState(
            id = rule.id,
            name = rule.name,
            enabled = rule.enabled,
            triggers = rule.triggers,
            constraints = rule.constraints,
            actions = rule.actions
        )
        destination = AutomationDestination.EDITOR
    }

    fun cancelEditing() {
        editorState = null
        nodeEditorState = null
        destination = AutomationDestination.RULES
    }

    fun updateRuleName(name: String) {
        editorState = editorState?.copy(name = name, validation = RuleValidationState())
    }

    fun updateRuleEnabled(enabled: Boolean) {
        editorState = editorState?.copy(enabled = enabled)
    }

    fun toggleRuleEnabled(rule: AutomationRule, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateEnabled(rule.id, enabled)
        }
    }

    fun deleteRule(rule: AutomationRule) {
        viewModelScope.launch {
            repository.deleteRule(rule.id)
            if (editorState?.id == rule.id) {
                cancelEditing()
            }
        }
    }

    fun saveRule() {
        val current = editorState ?: return
        val errors = validateRule(current)
        if (errors.isNotEmpty()) {
            editorState = current.copy(validation = RuleValidationState(errors))
            return
        }

        viewModelScope.launch {
            repository.upsertRule(
                AutomationRule(
                    id = current.id,
                    name = current.name.trim(),
                    enabled = current.enabled,
                    triggers = current.triggers,
                    constraints = current.constraints,
                    actions = current.actions
                )
            )
            cancelEditing()
        }
    }

    fun openNodeEditor(section: RuleSection, editingIndex: Int? = null) {
        val current = editorState ?: return
        val state = when (section) {
            RuleSection.TRIGGERS -> {
                val definitions = definitions.triggers
                val config = editingIndex?.let(current.triggers::getOrNull)
                val selected = (config?.type?.name ?: definitions.firstOrNull()?.type?.name).orEmpty()
                val values = config?.let {
                    definitions.firstOrNull { definition -> definition.type == config.type }?.valuesOf(config).orEmpty()
                } ?: defaultTriggerValues(selected)
                NodeEditorState(section, editingIndex, selected, values)
            }

            RuleSection.CONSTRAINTS -> {
                val definitions = definitions.constraints
                val config = editingIndex?.let(current.constraints::getOrNull)
                val selected = (config?.type?.name ?: definitions.firstOrNull()?.type?.name).orEmpty()
                val values = config?.let {
                    definitions.firstOrNull { definition -> definition.type == config.type }?.valuesOf(config).orEmpty()
                } ?: defaultConstraintValues(selected)
                NodeEditorState(section, editingIndex, selected, values)
            }

            RuleSection.ACTIONS -> {
                val definitions = definitions.actions
                val config = editingIndex?.let(current.actions::getOrNull)
                val selected = (config?.type?.name ?: definitions.firstOrNull()?.type?.name).orEmpty()
                val values = config?.let {
                    definitions.firstOrNull { definition -> definition.type == config.type }?.valuesOf(config).orEmpty()
                } ?: defaultActionValues(selected)
                NodeEditorState(section, editingIndex, selected, values)
            }
        }
        nodeEditorState = state
    }

    fun closeNodeEditor() {
        nodeEditorState = null
    }

    fun updateNodeType(typeKey: String) {
        val current = nodeEditorState ?: return
        nodeEditorState = current.copy(
            selectedTypeKey = typeKey,
            values = when (current.section) {
                RuleSection.TRIGGERS -> defaultTriggerValues(typeKey)
                RuleSection.CONSTRAINTS -> defaultConstraintValues(typeKey)
                RuleSection.ACTIONS -> defaultActionValues(typeKey)
            },
            errors = emptyList()
        )
    }

    fun updateNodeField(fieldId: String, value: String) {
        val current = nodeEditorState ?: return
        nodeEditorState = current.copy(
            values = current.values + (fieldId to value),
            errors = emptyList()
        )
    }

    fun commitNode() {
        val node = nodeEditorState ?: return
        val editor = editorState ?: return

        when (node.section) {
            RuleSection.TRIGGERS -> {
                val definition = definitions.trigger(TriggerType.valueOf(node.selectedTypeKey)) ?: return
                val errors = definition.validate(node.values)
                if (errors.isNotEmpty()) {
                    nodeEditorState = node.copy(errors = errors)
                    return
                }
                val config = definition.createConfig(node.values)
                editorState = editor.copy(
                    triggers = replaceAt(editor.triggers, node.editingIndex, config),
                    validation = RuleValidationState()
                )
            }

            RuleSection.CONSTRAINTS -> {
                val definition = definitions.constraint(ConstraintType.valueOf(node.selectedTypeKey)) ?: return
                val errors = definition.validate(node.values)
                if (errors.isNotEmpty()) {
                    nodeEditorState = node.copy(errors = errors)
                    return
                }
                val config = definition.createConfig(node.values)
                editorState = editor.copy(
                    constraints = replaceAt(editor.constraints, node.editingIndex, config),
                    validation = RuleValidationState()
                )
            }

            RuleSection.ACTIONS -> {
                val definition = definitions.action(ActionType.valueOf(node.selectedTypeKey)) ?: return
                val errors = definition.validate(node.values)
                if (errors.isNotEmpty()) {
                    nodeEditorState = node.copy(errors = errors)
                    return
                }
                val config = definition.createConfig(node.values)
                editorState = editor.copy(
                    actions = replaceAt(editor.actions, node.editingIndex, config),
                    validation = RuleValidationState()
                )
            }
        }

        closeNodeEditor()
    }

    fun deleteNode(section: RuleSection, index: Int) {
        val current = editorState ?: return
        editorState = when (section) {
            RuleSection.TRIGGERS -> current.copy(
                triggers = current.triggers.toMutableList().also { it.removeAt(index) },
                validation = RuleValidationState()
            )

            RuleSection.CONSTRAINTS -> current.copy(
                constraints = current.constraints.toMutableList().also { it.removeAt(index) },
                validation = RuleValidationState()
            )

            RuleSection.ACTIONS -> current.copy(
                actions = current.actions.toMutableList().also { it.removeAt(index) },
                validation = RuleValidationState()
            )
        }
    }

    fun summarizeTrigger(config: TriggerConfig): String =
        definitions.trigger(config.type)?.summarize(definitions.trigger(config.type)?.valuesOf(config).orEmpty())
            ?: config.type.name

    fun summarizeConstraint(config: ConstraintConfig): String =
        definitions.constraint(config.type)?.summarize(definitions.constraint(config.type)?.valuesOf(config).orEmpty())
            ?: config.type.name

    fun summarizeAction(config: ActionConfig): String =
        definitions.action(config.type)?.summarize(definitions.action(config.type)?.valuesOf(config).orEmpty())
            ?: config.type.name

    private fun validateRule(rule: RuleEditorState): List<String> {
        val errors = mutableListOf<String>()

        if (rule.name.isBlank()) {
            errors += "Rule name is required."
        }
        if (rule.triggers.isEmpty()) {
            errors += "At least one trigger is required."
        }
        if (rule.actions.isEmpty()) {
            errors += "At least one action is required."
        }

        rule.triggers.forEach { trigger ->
            errors += definitions.trigger(trigger.type)?.validate(
                definitions.trigger(trigger.type)?.valuesOf(trigger).orEmpty()
            ).orEmpty()
        }
        rule.constraints.forEach { constraint ->
            errors += definitions.constraint(constraint.type)?.validate(
                definitions.constraint(constraint.type)?.valuesOf(constraint).orEmpty()
            ).orEmpty()
        }
        rule.actions.forEach { action ->
            errors += definitions.action(action.type)?.validate(
                definitions.action(action.type)?.valuesOf(action).orEmpty()
            ).orEmpty()
        }

        return errors
    }

    private fun defaultTriggerValues(typeKey: String): Map<String, String> =
        definitions.trigger(TriggerType.valueOf(typeKey))?.fields?.associate { it.id to it.defaultValue }.orEmpty()

    private fun defaultConstraintValues(typeKey: String): Map<String, String> =
        definitions.constraint(ConstraintType.valueOf(typeKey))?.fields?.associate { it.id to it.defaultValue }.orEmpty()

    private fun defaultActionValues(typeKey: String): Map<String, String> =
        definitions.action(ActionType.valueOf(typeKey))?.fields?.associate { it.id to it.defaultValue }.orEmpty()

    companion object {
        fun factory(
            repository: AutomationRuleRepository,
            definitions: AutomationDefinitionRegistry
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AutomationRulesViewModel(repository, definitions) as T
            }
        }

        private fun <T> replaceAt(items: List<T>, index: Int?, value: T): List<T> {
            val mutable = items.toMutableList()
            if (index == null) {
                mutable += value
            } else {
                mutable[index] = value
            }
            return mutable
        }
    }
}
