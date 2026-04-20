package com.tomtruyen.orkestr.features.automation.viewmodel

import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.data.repository.AutomationNodeGroupRepository
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState
import com.tomtruyen.orkestr.features.automation.state.DefinitionCategoryGroup
import com.tomtruyen.orkestr.features.automation.state.DefinitionGroupListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleEditorState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.state.RuleValidationState
import com.tomtruyen.orkestr.ui.automation.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

class AutomationRuleEditorViewModel private constructor(
    private val stringResolver: StringResolver,
    private val repository: AutomationRuleRepository,
    private val groupRepository: AutomationNodeGroupRepository,
    private val definitions: AutomationDefinitionRegistry,
    customFlowDelegate: BindableAutomationRuleEditorCustomFlowDelegate =
        BindableAutomationRuleEditorCustomFlowDelegate(),
) : BaseViewModel<AutomationEditorUiState, AutomationEditorEvent, AutomationEditorAction>(
    initialState = AutomationEditorUiState(),
),
    AutomationRuleEditorCustomFlowDelegate by customFlowDelegate {
    constructor(
        stringResolver: StringResolver,
        repository: AutomationRuleRepository,
        groupRepository: AutomationNodeGroupRepository,
        definitions: AutomationDefinitionRegistry,
    ) : this(
        stringResolver = stringResolver,
        repository = repository,
        groupRepository = groupRepository,
        definitions = definitions,
        customFlowDelegate = BindableAutomationRuleEditorCustomFlowDelegate(),
    )

    init {
        observeGroups()
        customFlowDelegate.bind(
            definitions = definitions,
            stringResolver = stringResolver,
            state = { uiState.value },
            updateState = ::updateState,
            triggerEvent = ::triggerEvent,
        )
    }

    @Suppress("CyclomaticComplexMethod")
    override fun onAction(action: AutomationEditorAction) {
        when (action) {
            AutomationEditorAction.CloseEditorClicked -> closeEditorAndNavigateBack()

            AutomationEditorAction.ClosePickerClicked -> closePickerAndReturnToEditor()

            AutomationEditorAction.BackToPickerSelectionClicked -> navigateBackFromConfiguration()

            is AutomationEditorAction.RuleNameChanged -> updateRuleName(action.name)

            is AutomationEditorAction.RuleEnabledChanged -> updateRuleEnabled(action.enabled)

            is AutomationEditorAction.RuleActionExecutionModeChanged -> updateActionExecutionMode(action.executionMode)

            AutomationEditorAction.SaveRuleClicked -> saveRule()

            is AutomationEditorAction.AddNodeClicked -> startAddingNode(action.section)

            is AutomationEditorAction.EditNodeClicked -> editNode(action.section, action.index)

            is AutomationEditorAction.DeleteNodeClicked -> deleteNode(action.section, action.index)

            is AutomationEditorAction.SaveSectionAsGroupClicked -> saveSectionAsGroup(action.section, action.name)

            is AutomationEditorAction.SaveSelectedNodesAsGroupClicked ->
                saveSelectedNodesAsGroup(action.section, action.indices, action.name)

            is AutomationEditorAction.CreateConstraintConditionGroupClicked ->
                createConstraintConditionGroup(action.indices)

            is AutomationEditorAction.UpdateConstraintConditionGroupClicked ->
                updateConstraintConditionGroup(action.groupIndex, action.indices)

            is AutomationEditorAction.CopyConstraintConditionGroupClicked ->
                copyConstraintConditionGroup(action.groupIndex)

            is AutomationEditorAction.DeleteConstraintConditionGroupClicked ->
                deleteConstraintConditionGroup(action.groupIndex)

            is AutomationEditorAction.RemoveConstraintFromConditionGroupClicked ->
                removeConstraintFromConditionGroup(action.groupIndex, action.constraintIndex)

            is AutomationEditorAction.AddConstraintToConditionGroupClicked ->
                startAddingConstraintToConditionGroup(action.groupIndex)

            is AutomationEditorAction.PickerQueryChanged -> updatePickerQuery(action.query)

            is AutomationEditorAction.DefinitionSelected -> navigateToConfiguration(action.typeKey)

            is AutomationEditorAction.GroupSelected -> insertGroup(action.group)

            is AutomationEditorAction.PickerFieldChanged -> updatePickerField(action.fieldId, action.value)

            is AutomationEditorAction.SaveDraftAsGroupClicked -> saveDraftAsGroup(action.name)

            AutomationEditorAction.SavePickerClicked -> savePickerSelection()
        }
    }

    fun setCreateRule() {
        updateState {
            it.copy(
                editorState = RuleEditorState(id = UUID.randomUUID().toString()),
                pickerState = null,
            )
        }
    }

    fun setEditRule(rule: AutomationRule) {
        updateState {
            it.copy(
                editorState = RuleEditorState(
                    id = rule.id,
                    name = rule.name,
                    enabled = rule.enabled,
                    triggers = rule.triggers,
                    constraints = rule.constraints,
                    constraintGroups = rule.constraintGroups,
                    actions = rule.actions,
                    actionExecutionMode = rule.actionExecutionMode,
                ),
                pickerState = null,
            )
        }
    }

    fun definitionItems(section: RuleSection, query: String): List<DefinitionListItem> =
        definitions.definitionItems(section, query, stringResolver)

    fun definitionCategoryGroups(section: RuleSection, query: String): List<DefinitionCategoryGroup> =
        definitions.definitionCategoryGroups(section, query, stringResolver)

    fun groupDefinitionCategoryGroups(type: AutomationNodeGroupType, query: String): List<DefinitionCategoryGroup> =
        definitions.definitionCategoryGroups(type.toRuleSection(), query, stringResolver)

    fun defaultGroupNodeConfig(type: AutomationNodeGroupType, typeKey: String): AutomationConfig<*>? =
        definitions.defaultConfig(type.toRuleSection(), typeKey)

    fun definitionGroupItems(section: RuleSection, query: String): List<DefinitionGroupListItem> {
        val normalizedQuery = query.trim()
        return uiState.value.groups
            .filter { it.type == section.toGroupType() }
            .filter { normalizedQuery.isBlank() || it.name.contains(normalizedQuery, ignoreCase = true) }
            .map { group ->
                DefinitionGroupListItem(
                    group = group,
                    summaries = when (section) {
                        RuleSection.TRIGGERS -> group.triggers.map(::summarizeTrigger)
                        RuleSection.CONSTRAINTS -> group.constraints.map(::summarizeConstraint)
                        RuleSection.ACTIONS -> group.actions.map(::summarizeAction)
                    },
                )
            }
    }

    fun summarizeTrigger(config: TriggerConfig): String =
        definitions.trigger(config.type)?.summarizeAny(config, stringResolver)
            ?: config.type.name

    fun summarizeConstraint(config: ConstraintConfig): String =
        definitions.constraint(config.type)?.summarizeAny(config, stringResolver)
            ?: config.type.name

    fun summarizeAction(config: ActionConfig): String =
        definitions.action(config.type)?.summarizeAny(config, stringResolver)
            ?: config.type.name

    fun selectedDefinitionItem(): DefinitionListItem? = definitions.selectedDefinitionItem(uiState.value.pickerState)

    fun applyConfiguredTrigger(config: TriggerConfig) {
        val picker = uiState.value.pickerState ?: return
        val editor = uiState.value.editorState ?: return
        updateState {
            it.copy(
                editorState = editor.withTrigger(config, picker.editingIndex),
                pickerState = null,
            )
        }
        triggerEvent(AutomationEditorEvent.PopToEditor)
    }

    fun requiredPermissionsForNode(section: RuleSection, index: Int): List<AutomationPermission> {
        val editor = uiState.value.editorState ?: return emptyList()
        return editor.requiredPermissionsForNode(section, index)
    }

    fun requiredPermissionsForGroup(group: AutomationNodeGroup): List<AutomationPermission> =
        group.triggers.flatMap { it.requiredPermissions } +
            group.constraints.flatMap { it.requiredPermissions } +
            group.actions.flatMap { it.requiredPermissions }

    private fun closeEditor() {
        updateState { it.copy(editorState = null, pickerState = null) }
    }

    private fun observeGroups() = groupRepository.observeGroups()
        .onEach { groups ->
            updateState { it.copy(groups = groups) }
        }.launchIn(viewModelScope)

    private fun closeEditorAndNavigateBack() {
        closeEditor()
        triggerEvent(AutomationEditorEvent.NavigateBackToRules)
    }

    private fun closePicker() {
        updateState { it.copy(pickerState = null) }
    }

    private fun closePickerAndReturnToEditor() {
        closePicker()
        triggerEvent(AutomationEditorEvent.PopToEditor)
    }

    private fun updateRuleName(name: String) {
        val editor = uiState.value.editorState ?: return
        updateState {
            it.copy(
                editorState = editor.copy(
                    name = name,
                    validation = RuleValidationState(),
                ),
            )
        }
    }

    private fun updateRuleEnabled(enabled: Boolean) {
        val editor = uiState.value.editorState ?: return
        updateState { it.copy(editorState = editor.copy(enabled = enabled)) }
    }

    private fun updateActionExecutionMode(executionMode: ActionExecutionMode) {
        val editor = uiState.value.editorState ?: return
        updateState { it.copy(editorState = editor.withActionExecutionMode(executionMode)) }
    }

    private fun startAddingNode(section: RuleSection) {
        startSelection(section, null, launchedFromSelection = true)
        triggerEvent(
            AutomationEditorEvent.NavigateToDefinitionSelection(
                section = section,
                editingIndex = null,
            ),
        )
    }

    private fun startAddingConstraintToConditionGroup(groupIndex: Int) {
        startSelection(
            section = RuleSection.CONSTRAINTS,
            editingIndex = null,
            launchedFromSelection = true,
            targetConstraintGroupIndex = groupIndex,
        )
        triggerEvent(
            AutomationEditorEvent.NavigateToDefinitionSelection(
                section = RuleSection.CONSTRAINTS,
                editingIndex = null,
            ),
        )
    }

    private fun editNode(section: RuleSection, index: Int) {
        startSelection(section, index, launchedFromSelection = false)
        val picker = uiState.value.pickerState ?: return
        val typeKey = picker.selectedTypeKey ?: return
        openConfiguration(typeKey)
        triggerEvent(defaultConfigurationEvent(picker, typeKey))
    }

    private fun updatePickerQuery(query: String) {
        val picker = uiState.value.pickerState ?: return
        updateState { it.copy(pickerState = picker.copy(query = query)) }
    }

    private fun updatePickerField(fieldId: String, value: String) {
        val picker = uiState.value.pickerState ?: return
        val typeKey = picker.selectedTypeKey ?: return
        val definition = definitions.definitionFor(picker.section, typeKey) ?: return
        updateState {
            it.copy(
                pickerState = picker.copy(
                    draftConfig = definition.updateFieldAny(
                        config = picker.draftConfig,
                        fieldId = fieldId,
                        value = value,
                    ),
                    errors = emptyList(),
                ),
            )
        }
    }

    private fun startSelection(
        section: RuleSection,
        editingIndex: Int?,
        launchedFromSelection: Boolean,
        targetConstraintGroupIndex: Int? = null,
    ) {
        val current = uiState.value.editorState ?: return
        val existing = current.nodeAt(section, editingIndex)

        val pickerState = if (existing == null) {
            DefinitionPickerState(
                section = section,
                editingIndex = editingIndex,
                targetConstraintGroupIndex = targetConstraintGroupIndex,
                launchedFromSelection = launchedFromSelection,
            )
        } else {
            DefinitionPickerState(
                section = section,
                editingIndex = editingIndex,
                targetConstraintGroupIndex = targetConstraintGroupIndex,
                launchedFromSelection = launchedFromSelection,
                selectedTypeKey = existing.type.name,
                draftConfig = existing,
            )
        }

        updateState { it.copy(pickerState = pickerState) }
    }

    private fun openConfiguration(typeKey: String) {
        val picker = uiState.value.pickerState ?: return
        updateState {
            it.copy(
                pickerState = picker.copy(
                    selectedTypeKey = typeKey,
                    draftConfig = if (picker.selectedTypeKey == typeKey && picker.draftConfig != null) {
                        picker.draftConfig
                    } else {
                        definitions.defaultConfig(picker.section, typeKey)
                    },
                    errors = emptyList(),
                ),
            )
        }
    }

    private fun navigateToConfiguration(typeKey: String) {
        openConfiguration(typeKey)
        val picker = uiState.value.pickerState ?: return
        triggerEvent(
            definitions.customNavigationEventFor(picker.section, typeKey) ?: defaultConfigurationEvent(picker, typeKey),
        )
    }

    private fun navigateBackFromConfiguration() {
        val picker = uiState.value.pickerState ?: return
        if (picker.launchedFromSelection) {
            backToPickerList()
            triggerEvent(AutomationEditorEvent.PopToDefinitionSelection)
        } else {
            closePicker()
            triggerEvent(AutomationEditorEvent.PopToEditor)
        }
    }

    private fun backToPickerList() {
        val picker = uiState.value.pickerState ?: return
        updateState {
            it.copy(
                pickerState = picker.copy(
                    selectedTypeKey = null,
                    draftConfig = null,
                    errors = emptyList(),
                ),
            )
        }
    }

    private fun saveRule() {
        val current = uiState.value.editorState ?: return
        val errors = validateRule(current)
        if (errors.isNotEmpty()) {
            updateState { it.copy(editorState = current.copy(validation = RuleValidationState(errors))) }
            return
        }

        launch {
            repository.upsertRule(
                AutomationRule(
                    id = current.id,
                    name = current.name.trim(),
                    enabled = current.enabled,
                    triggers = current.triggers,
                    constraints = current.constraints,
                    constraintGroups = current.constraintGroups,
                    actions = current.actions,
                    actionExecutionMode = current.actionExecutionMode,
                ),
            )
            closeEditor()
            triggerEvent(AutomationEditorEvent.NavigateBackToRules)
        }
    }

    private fun savePickerSelection() {
        val picker = uiState.value.pickerState ?: return
        val editor = uiState.value.editorState ?: return
        val typeKey = picker.selectedTypeKey ?: return

        when (picker.section) {
            RuleSection.TRIGGERS -> {
                val definition = definitions.trigger(TriggerType.valueOf(typeKey)) ?: return
                if (!saveNodeSelection(
                        picker = picker,
                        editor = editor,
                        definition = definition,
                        updateEditor = { currentEditor, config ->
                            currentEditor.withTrigger(config as TriggerConfig, picker.editingIndex)
                        },
                    )
                ) {
                    return
                }
            }

            RuleSection.CONSTRAINTS -> {
                val definition = definitions.constraint(ConstraintType.valueOf(typeKey)) ?: return
                if (!saveNodeSelection(
                        picker = picker,
                        editor = editor,
                        definition = definition,
                        updateEditor = { currentEditor, config ->
                            val constraint = config as ConstraintConfig
                            if (picker.targetConstraintGroupIndex != null && picker.editingIndex == null) {
                                currentEditor.withConstraintInConditionGroup(
                                    config = constraint,
                                    groupIndex = picker.targetConstraintGroupIndex,
                                )
                            } else {
                                currentEditor.withConstraint(constraint, picker.editingIndex)
                            }
                        },
                    )
                ) {
                    return
                }
            }

            RuleSection.ACTIONS -> {
                val definition = definitions.action(ActionType.valueOf(typeKey)) ?: return
                if (!saveNodeSelection(
                        picker = picker,
                        editor = editor,
                        definition = definition,
                        updateEditor = { currentEditor, config ->
                            currentEditor.withAction(config as ActionConfig, picker.editingIndex)
                        },
                    )
                ) {
                    return
                }
            }
        }

        triggerEvent(AutomationEditorEvent.PopToEditor)
    }

    private fun saveDraftAsGroup(name: String) {
        val picker = uiState.value.pickerState ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        val draftConfig = picker.draftConfig ?: return
        val typeKey = picker.selectedTypeKey ?: return
        val definition = definitions.definitionFor(picker.section, typeKey) ?: return
        val errors = definition.validateAny(draftConfig, stringResolver)
        if (errors.isNotEmpty()) {
            updateState { it.copy(pickerState = picker.copy(errors = errors)) }
            return
        }
        val group = when (picker.section) {
            RuleSection.TRIGGERS -> AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = AutomationNodeGroupType.TRIGGER,
                triggers = listOf(draftConfig as? TriggerConfig ?: return),
            )

            RuleSection.CONSTRAINTS -> AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = AutomationNodeGroupType.CONSTRAINT,
                constraints = listOf(draftConfig as? ConstraintConfig ?: return),
            )

            RuleSection.ACTIONS -> AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = AutomationNodeGroupType.ACTION,
                actions = listOf(draftConfig as? ActionConfig ?: return),
            )
        }
        launch { groupRepository.upsertGroup(group) }
    }

    private fun insertGroup(group: AutomationNodeGroup) {
        val picker = uiState.value.pickerState ?: return
        val editor = uiState.value.editorState ?: return
        if (group.type != picker.section.toGroupType()) return
        updateState {
            it.copy(
                editorState = when (picker.section) {
                    RuleSection.TRIGGERS -> editor.copy(
                        triggers = editor.triggers + group.triggers,
                        validation = RuleValidationState(),
                    )

                    RuleSection.CONSTRAINTS -> editor.copy(
                        constraints = editor.constraints + group.constraints,
                        constraintGroups = if (editor.constraintGroups.isEmpty() || group.constraints.isEmpty()) {
                            emptyList()
                        } else {
                            editor.constraintGroups + ConstraintGroup(group.constraints)
                        },
                        validation = RuleValidationState(),
                    )

                    RuleSection.ACTIONS -> editor.copy(
                        actions = editor.actions + group.actions,
                        validation = RuleValidationState(),
                    )
                },
                pickerState = null,
            )
        }
        triggerEvent(AutomationEditorEvent.PopToEditor)
    }

    private fun deleteNode(section: RuleSection, index: Int) {
        val current = uiState.value.editorState ?: return
        updateState { it.copy(editorState = current.withNodeRemoved(section, index)) }
    }

    private fun saveSectionAsGroup(section: RuleSection, name: String) {
        val editor = uiState.value.editorState ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        val group = when (section) {
            RuleSection.TRIGGERS -> {
                if (editor.triggers.isEmpty()) return
                AutomationNodeGroup(
                    id = UUID.randomUUID().toString(),
                    name = trimmedName,
                    type = AutomationNodeGroupType.TRIGGER,
                    triggers = editor.triggers,
                )
            }

            RuleSection.CONSTRAINTS -> {
                if (editor.constraints.isEmpty()) return
                AutomationNodeGroup(
                    id = UUID.randomUUID().toString(),
                    name = trimmedName,
                    type = AutomationNodeGroupType.CONSTRAINT,
                    constraints = editor.constraints,
                )
            }

            RuleSection.ACTIONS -> {
                if (editor.actions.isEmpty()) return
                AutomationNodeGroup(
                    id = UUID.randomUUID().toString(),
                    name = trimmedName,
                    type = AutomationNodeGroupType.ACTION,
                    actions = editor.actions,
                )
            }
        }
        launch { groupRepository.upsertGroup(group) }
    }

    private fun saveSelectedNodesAsGroup(section: RuleSection, indices: Set<Int>, name: String) {
        val editor = uiState.value.editorState ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        val selectedIndices = indices.sorted()
        if (selectedIndices.isEmpty()) return

        val group = when (section) {
            RuleSection.TRIGGERS -> AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = AutomationNodeGroupType.TRIGGER,
                triggers = selectedIndices.mapNotNull(editor.triggers::getOrNull),
            ).takeIf { it.triggers.isNotEmpty() }

            RuleSection.CONSTRAINTS -> AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = AutomationNodeGroupType.CONSTRAINT,
                constraints = selectedIndices.mapNotNull(editor.constraints::getOrNull),
            ).takeIf { it.constraints.isNotEmpty() }

            RuleSection.ACTIONS -> AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = AutomationNodeGroupType.ACTION,
                actions = selectedIndices.mapNotNull(editor.actions::getOrNull),
            ).takeIf { it.actions.isNotEmpty() }
        } ?: return

        launch { groupRepository.upsertGroup(group) }
    }

    private fun createConstraintConditionGroup(indices: Set<Int>) {
        val editor = uiState.value.editorState ?: return
        updateState { it.copy(editorState = editor.withConstraintConditionGroup(indices)) }
    }

    private fun updateConstraintConditionGroup(groupIndex: Int, indices: Set<Int>) {
        val editor = uiState.value.editorState ?: return
        updateState { it.copy(editorState = editor.withConstraintConditionGroupUpdated(groupIndex, indices)) }
    }

    private fun deleteConstraintConditionGroup(groupIndex: Int) {
        val editor = uiState.value.editorState ?: return
        updateState { it.copy(editorState = editor.withConstraintConditionGroupDeleted(groupIndex)) }
    }

    private fun copyConstraintConditionGroup(groupIndex: Int) {
        val editor = uiState.value.editorState ?: return
        updateState { it.copy(editorState = editor.withConstraintConditionGroupCopied(groupIndex)) }
    }

    private fun removeConstraintFromConditionGroup(groupIndex: Int, constraintIndex: Int) {
        val editor = uiState.value.editorState ?: return
        updateState {
            it.copy(
                editorState = editor.withConstraintRemovedFromConditionGroup(
                    groupIndex = groupIndex,
                    constraintIndex = constraintIndex,
                ),
            )
        }
    }

    private fun validateRule(rule: RuleEditorState): List<String> {
        val errors = mutableListOf<String>()

        if (rule.name.isBlank()) {
            errors += stringResolver.resolve(R.string.automation_error_rule_name_required)
        }
        if (rule.triggers.isEmpty()) {
            errors += stringResolver.resolve(R.string.automation_error_trigger_required)
        }
        if (rule.actions.isEmpty()) {
            errors += stringResolver.resolve(R.string.automation_error_action_required)
        }

        rule.triggers.forEach { trigger ->
            errors += definitions.trigger(trigger.type)?.validateAny(trigger, stringResolver).orEmpty()
        }
        rule.constraints.forEach { constraint ->
            errors += definitions.constraint(constraint.type)?.validateAny(constraint, stringResolver).orEmpty()
        }
        rule.actions.forEach { action ->
            errors += definitions.action(action.type)?.validateAny(action, stringResolver).orEmpty()
        }

        return errors
    }

    private fun defaultConfigurationEvent(picker: DefinitionPickerState, typeKey: String) =
        AutomationEditorEvent.NavigateToDefinitionConfiguration(
            section = picker.section,
            typeKey = typeKey,
            editingIndex = picker.editingIndex,
        )

    private fun saveNodeSelection(
        picker: DefinitionPickerState,
        editor: RuleEditorState,
        definition: AutomationNodeDefinition<*, *>,
        updateEditor: (RuleEditorState, AutomationConfig<*>) -> RuleEditorState,
    ): Boolean {
        val errors = definition.validateAny(picker.draftConfig, stringResolver)
        if (errors.isNotEmpty()) {
            updateState { it.copy(pickerState = picker.copy(errors = errors)) }
            return false
        }

        val config = definition.cast(picker.draftConfig) ?: definition.initialConfig()
        updateState {
            it.copy(
                editorState = updateEditor(editor, config),
                pickerState = null,
            )
        }
        return true
    }
}

private fun RuleSection.toGroupType(): AutomationNodeGroupType = when (this) {
    RuleSection.TRIGGERS -> AutomationNodeGroupType.TRIGGER
    RuleSection.CONSTRAINTS -> AutomationNodeGroupType.CONSTRAINT
    RuleSection.ACTIONS -> AutomationNodeGroupType.ACTION
}

private fun AutomationNodeGroupType.toRuleSection(): RuleSection = when (this) {
    AutomationNodeGroupType.TRIGGER -> RuleSection.TRIGGERS
    AutomationNodeGroupType.CONSTRAINT -> RuleSection.CONSTRAINTS
    AutomationNodeGroupType.ACTION -> RuleSection.ACTIONS
}

private fun AutomationNodeGroup.nodeAt(index: Int): AutomationConfig<*>? = when (type) {
    AutomationNodeGroupType.TRIGGER -> triggers.getOrNull(index)
    AutomationNodeGroupType.CONSTRAINT -> constraints.getOrNull(index)
    AutomationNodeGroupType.ACTION -> actions.getOrNull(index)
}
