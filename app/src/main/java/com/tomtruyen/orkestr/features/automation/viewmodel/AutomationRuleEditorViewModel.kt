package com.tomtruyen.orkestr.features.automation.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.data.definition.AutomationNodeDefinition
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleEditorState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.state.RuleValidationState
import java.util.UUID
import kotlinx.coroutines.launch

class AutomationRuleEditorViewModel(
    private val context: Context,
    private val repository: AutomationRuleRepository,
    private val definitions: AutomationDefinitionRegistry
) : ViewModel() {
    var editorState by mutableStateOf<RuleEditorState?>(null)
        private set

    var pickerState by mutableStateOf<DefinitionPickerState?>(null)
        private set

    fun createRule() {
        editorState = RuleEditorState(id = UUID.randomUUID().toString())
        pickerState = null
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
        pickerState = null
    }

    fun closeEditor() {
        editorState = null
        pickerState = null
    }

    fun closePicker() {
        pickerState = null
    }

    fun updateRuleName(name: String) {
        editorState = editorState?.copy(name = name, validation = RuleValidationState())
    }

    fun updateRuleEnabled(enabled: Boolean) {
        editorState = editorState?.copy(enabled = enabled)
    }

    fun saveRule(onSaved: () -> Unit) {
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
            closeEditor()
            onSaved()
        }
    }

    fun startSelection(section: RuleSection, editingIndex: Int? = null) {
        val current = editorState ?: return
        val existing = when (section) {
            RuleSection.TRIGGERS -> editingIndex?.let(current.triggers::getOrNull)
            RuleSection.CONSTRAINTS -> editingIndex?.let(current.constraints::getOrNull)
            RuleSection.ACTIONS -> editingIndex?.let(current.actions::getOrNull)
        }

        pickerState = if (existing == null) {
            DefinitionPickerState(section = section, editingIndex = editingIndex)
        } else {
            DefinitionPickerState(
                section = section,
                editingIndex = editingIndex,
                selectedTypeKey = definitionKeyOf(existing),
                values = valuesOf(section, existing)
            )
        }
    }

    fun openConfiguration(typeKey: String) {
        val state = pickerState ?: return
        pickerState = state.copy(
            selectedTypeKey = typeKey,
            values = if (state.selectedTypeKey == typeKey && state.values.isNotEmpty()) {
                state.values
            } else {
                defaultValues(state.section, typeKey)
            },
            errors = emptyList()
        )
    }

    fun updatePickerQuery(query: String) {
        pickerState = pickerState?.copy(query = query)
    }

    fun backToPickerList() {
        val state = pickerState ?: return
        pickerState = state.copy(
            selectedTypeKey = null,
            values = emptyMap(),
            errors = emptyList()
        )
    }

    fun updatePickerField(fieldId: String, value: String) {
        val state = pickerState ?: return
        pickerState = state.copy(
            values = state.values + (fieldId to value),
            errors = emptyList()
        )
    }

    fun savePickerSelection() {
        val picker = pickerState ?: return
        val editor = editorState ?: return
        val typeKey = picker.selectedTypeKey ?: return

        when (picker.section) {
            RuleSection.TRIGGERS -> {
                val definition = definitions.trigger(TriggerType.valueOf(typeKey)) ?: return
                val errors = definition.validate(picker.values)
                if (errors.isNotEmpty()) {
                    pickerState = picker.copy(errors = errors)
                    return
                }
                editorState = editor.copy(
                    triggers = replaceAt(editor.triggers, picker.editingIndex, definition.createConfig(picker.values)),
                    validation = RuleValidationState()
                )
            }

            RuleSection.CONSTRAINTS -> {
                val definition = definitions.constraint(ConstraintType.valueOf(typeKey)) ?: return
                val errors = definition.validate(picker.values)
                if (errors.isNotEmpty()) {
                    pickerState = picker.copy(errors = errors)
                    return
                }
                editorState = editor.copy(
                    constraints = replaceAt(editor.constraints, picker.editingIndex, definition.createConfig(picker.values)),
                    validation = RuleValidationState()
                )
            }

            RuleSection.ACTIONS -> {
                val definition = definitions.action(ActionType.valueOf(typeKey)) ?: return
                val errors = definition.validate(picker.values)
                if (errors.isNotEmpty()) {
                    pickerState = picker.copy(errors = errors)
                    return
                }
                editorState = editor.copy(
                    actions = replaceAt(editor.actions, picker.editingIndex, definition.createConfig(picker.values)),
                    validation = RuleValidationState()
                )
            }
        }

        closePicker()
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

    fun definitionItems(section: RuleSection, query: String): List<DefinitionListItem> =
        definitionsFor(section)
            .filter { definition ->
                query.isBlank() ||
                    definition.title.contains(query, ignoreCase = true) ||
                    definition.description.contains(query, ignoreCase = true)
            }
            .map { definition ->
                DefinitionListItem(
                    key = definition.key,
                    title = definition.title,
                    description = definition.description,
                    fields = definition.fields
                )
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
            errors += context.getString(R.string.automation_error_rule_name_required)
        }
        if (rule.triggers.isEmpty()) {
            errors += context.getString(R.string.automation_error_trigger_required)
        }
        if (rule.actions.isEmpty()) {
            errors += context.getString(R.string.automation_error_action_required)
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

    private fun definitionKeyOf(config: Any): String = when (config) {
        is TriggerConfig -> config.type.name
        is ConstraintConfig -> config.type.name
        is ActionConfig -> config.type.name
        else -> error("Unsupported config type: ${config::class.qualifiedName}")
    }

    private fun valuesOf(section: RuleSection, config: Any): Map<String, String> = when (section) {
        RuleSection.TRIGGERS -> definitions.trigger((config as TriggerConfig).type)?.valuesOf(config).orEmpty()
        RuleSection.CONSTRAINTS -> definitions.constraint((config as ConstraintConfig).type)?.valuesOf(config).orEmpty()
        RuleSection.ACTIONS -> definitions.action((config as ActionConfig).type)?.valuesOf(config).orEmpty()
    }

    private fun defaultValues(section: RuleSection, typeKey: String): Map<String, String> = when (section) {
        RuleSection.TRIGGERS -> definitions.trigger(TriggerType.valueOf(typeKey))?.fields
        RuleSection.CONSTRAINTS -> definitions.constraint(ConstraintType.valueOf(typeKey))?.fields
        RuleSection.ACTIONS -> definitions.action(ActionType.valueOf(typeKey))?.fields
    }?.associate { it.id to it.defaultValue }.orEmpty()

    private fun definitionsFor(section: RuleSection): List<AutomationNodeDefinition> = when (section) {
        RuleSection.TRIGGERS -> definitions.triggers
        RuleSection.CONSTRAINTS -> definitions.constraints
        RuleSection.ACTIONS -> definitions.actions
    }

    companion object {
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
