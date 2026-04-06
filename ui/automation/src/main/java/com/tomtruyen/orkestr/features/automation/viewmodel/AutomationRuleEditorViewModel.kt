package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState
import com.tomtruyen.orkestr.features.automation.state.DefinitionCategoryGroup
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleEditorState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.state.RuleValidationState
import com.tomtruyen.orkestr.ui.automation.R
import java.util.UUID

class AutomationRuleEditorViewModel(
    private val stringResolver: StringResolver,
    private val repository: AutomationRuleRepository,
    private val definitions: AutomationDefinitionRegistry,
) : BaseViewModel<AutomationEditorUiState, AutomationEditorEvent, AutomationEditorAction>(
    initialState = AutomationEditorUiState(),
) {
    override fun onAction(action: AutomationEditorAction) {
        when (action) {
            AutomationEditorAction.CloseEditorClicked -> closeEditorAndNavigateBack()
            AutomationEditorAction.ClosePickerClicked -> closePickerAndReturnToEditor()
            AutomationEditorAction.BackToPickerSelectionClicked -> navigateBackFromConfiguration()
            is AutomationEditorAction.RuleNameChanged -> updateRuleName(action.name)
            is AutomationEditorAction.RuleEnabledChanged -> updateRuleEnabled(action.enabled)
            AutomationEditorAction.SaveRuleClicked -> saveRule()
            is AutomationEditorAction.AddNodeClicked -> startAddingNode(action.section)
            is AutomationEditorAction.EditNodeClicked -> editNode(action.section, action.index)
            is AutomationEditorAction.DeleteNodeClicked -> deleteNode(action.section, action.index)
            is AutomationEditorAction.PickerQueryChanged -> updatePickerQuery(action.query)
            is AutomationEditorAction.DefinitionSelected -> navigateToConfiguration(action.typeKey)
            is AutomationEditorAction.PickerFieldChanged -> updatePickerField(action.fieldId, action.value)
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
                    actions = rule.actions,
                ),
                pickerState = null,
            )
        }
    }

    fun definitionItems(section: RuleSection, query: String): List<DefinitionListItem> =
        definitions.definitionItems(section, query, stringResolver)

    fun definitionCategoryGroups(section: RuleSection, query: String): List<DefinitionCategoryGroup> =
        definitions.definitionCategoryGroups(section, query, stringResolver)

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

    fun currentGeofenceTriggerConfig(): GeofenceTriggerConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? GeofenceTriggerConfig ?: GeofenceTriggerConfig()
    }

    fun applySelectedGeofence(config: GeofenceTriggerConfig) {
        val picker = uiState.value.pickerState ?: return
        updateState {
            it.copy(
                pickerState = picker.copy(
                    draftConfig = config,
                    errors = emptyList(),
                ),
            )
        }
        triggerEvent(
            AutomationEditorEvent.NavigateToDefinitionConfiguration(
                section = picker.section,
                typeKey = config.type.name,
                editingIndex = picker.editingIndex,
            ),
        )
    }

    fun currentNotificationTriggerConfig(): NotificationReceivedTriggerConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? NotificationReceivedTriggerConfig ?: NotificationReceivedTriggerConfig()
    }

    fun currentApplicationLifecycleTriggerConfig(): ApplicationLifecycleTriggerConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? ApplicationLifecycleTriggerConfig ?: ApplicationLifecycleTriggerConfig()
    }

    fun currentLaunchApplicationActionConfig(): LaunchApplicationActionConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? LaunchApplicationActionConfig ?: LaunchApplicationActionConfig()
    }

    fun currentSetWallpaperActionConfig(): SetWallpaperActionConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? SetWallpaperActionConfig ?: SetWallpaperActionConfig()
    }

    fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String) {
        when (selectedTypeKey) {
            TriggerType.APPLICATION_LIFECYCLE.name -> {
                val current = currentApplicationLifecycleTriggerConfig()
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }

            ActionType.LAUNCH_APPLICATION.name -> {
                val current = currentLaunchApplicationActionConfig()
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName, appLabel = appLabel))
            }

            else -> {
                val current = currentNotificationTriggerConfig()
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }
        }
    }

    fun currentWifiTriggerConfig(): WifiSsidTriggerConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? WifiSsidTriggerConfig ?: WifiSsidTriggerConfig()
    }

    fun applySelectedWifiTrigger(config: WifiSsidTriggerConfig) {
        applyDraftConfigAndOpenConfiguration(config)
    }

    fun applySelectedWallpaper(imageUri: String, imageLabel: String) {
        val current = currentSetWallpaperActionConfig()
        val picker = uiState.value.pickerState ?: return
        updateState {
            it.copy(
                pickerState = picker.copy(
                    draftConfig = current.copy(imageUri = imageUri, imageLabel = imageLabel),
                    errors = emptyList(),
                ),
            )
        }
    }

    fun currentTimeBasedTriggerConfig(): TimeBasedTriggerConfig {
        val draft = uiState.value.pickerState?.draftConfig
        return draft as? TimeBasedTriggerConfig ?: TimeBasedTriggerConfig()
    }

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

    private fun closeEditor() {
        updateState { it.copy(editorState = null, pickerState = null) }
    }

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

    private fun startAddingNode(section: RuleSection) {
        startSelection(section, null, launchedFromSelection = true)
        triggerEvent(
            AutomationEditorEvent.NavigateToDefinitionSelection(
                section = section,
                editingIndex = null,
            ),
        )
    }

    private fun editNode(section: RuleSection, index: Int) {
        startSelection(section, index, launchedFromSelection = false)
        uiState.value.pickerState?.selectedTypeKey?.let(::navigateToConfiguration)
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

    private fun startSelection(section: RuleSection, editingIndex: Int?, launchedFromSelection: Boolean) {
        val current = uiState.value.editorState ?: return
        val existing = current.nodeAt(section, editingIndex)

        val pickerState = if (existing == null) {
            DefinitionPickerState(
                section = section,
                editingIndex = editingIndex,
                launchedFromSelection = launchedFromSelection,
            )
        } else {
            DefinitionPickerState(
                section = section,
                editingIndex = editingIndex,
                launchedFromSelection = launchedFromSelection,
                selectedTypeKey = definitionKeyOf(existing),
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
                    actions = current.actions,
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
                            currentEditor.withConstraint(config as ConstraintConfig, picker.editingIndex)
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

    private fun deleteNode(section: RuleSection, index: Int) {
        val current = uiState.value.editorState ?: return
        updateState { it.copy(editorState = current.withNodeRemoved(section, index)) }
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

    private fun definitionKeyOf(config: Any): String = when (config) {
        is TriggerConfig -> config.type.name
        is ConstraintConfig -> config.type.name
        is ActionConfig -> config.type.name
        else -> error("Unsupported config type: ${config::class.qualifiedName}")
    }

    private fun applyDraftConfigAndOpenConfiguration(config: AutomationConfig<*>) {
        val picker = uiState.value.pickerState ?: return
        updateState {
            it.copy(
                pickerState = picker.copy(
                    draftConfig = config,
                    errors = emptyList(),
                ),
            )
        }
        triggerEvent(defaultConfigurationEvent(picker, definitionKeyOf(config)))
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
