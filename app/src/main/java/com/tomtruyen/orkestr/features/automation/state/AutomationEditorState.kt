package com.tomtruyen.orkestr.features.automation.state

import androidx.annotation.StringRes
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.data.definition.AutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.orkestr.R

enum class RuleSection(
    @param:StringRes val titleRes: Int,
    @param:StringRes val helperRes: Int,
    @param:StringRes val singularTitleRes: Int
) {
    TRIGGERS(
        titleRes = R.string.automation_section_triggers_title,
        helperRes = R.string.automation_section_triggers_helper,
        singularTitleRes = R.string.automation_singular_trigger
    ),
    CONSTRAINTS(
        titleRes = R.string.automation_section_constraints_title,
        helperRes = R.string.automation_section_constraints_helper,
        singularTitleRes = R.string.automation_singular_constraint
    ),
    ACTIONS(
        titleRes = R.string.automation_section_actions_title,
        helperRes = R.string.automation_section_actions_helper,
        singularTitleRes = R.string.automation_singular_action
    )
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

data class DefinitionListItem(
    val key: String,
    val title: String,
    val description: String,
    val fields: List<AutomationFieldDefinition>,
    val permissions: List<AutomationPermission> = emptyList()
)

data class DefinitionPickerState(
    val section: RuleSection,
    val editingIndex: Int? = null,
    val query: String = "",
    val launchedFromSelection: Boolean = true,
    val selectedTypeKey: String? = null,
    val values: Map<String, String> = emptyMap(),
    val errors: List<String> = emptyList()
)

data class AutomationRulesUiState(
    val rules: List<AutomationRule> = emptyList()
)

data class AutomationEditorUiState(
    val editorState: RuleEditorState? = null,
    val pickerState: DefinitionPickerState? = null
)

sealed interface AutomationRulesAction {
    data object CreateRuleClicked : AutomationRulesAction
    data class EditRuleClicked(val rule: AutomationRule) : AutomationRulesAction
    data class DeleteRuleClicked(val rule: AutomationRule) : AutomationRulesAction
    data class ToggleRuleEnabled(val rule: AutomationRule, val enabled: Boolean) : AutomationRulesAction
}

sealed interface AutomationRulesEvent {
    data object NavigateToCreateRule : AutomationRulesEvent
    data class NavigateToEditRule(val rule: AutomationRule) : AutomationRulesEvent
}

sealed interface AutomationEditorAction {
    data object CloseEditorClicked : AutomationEditorAction
    data object ClosePickerClicked : AutomationEditorAction
    data object BackToPickerSelectionClicked : AutomationEditorAction
    data class RuleNameChanged(val name: String) : AutomationEditorAction
    data class RuleEnabledChanged(val enabled: Boolean) : AutomationEditorAction
    data object SaveRuleClicked : AutomationEditorAction
    data class AddNodeClicked(val section: RuleSection) : AutomationEditorAction
    data class EditNodeClicked(val section: RuleSection, val index: Int) : AutomationEditorAction
    data class DeleteNodeClicked(val section: RuleSection, val index: Int) : AutomationEditorAction
    data class PickerQueryChanged(val query: String) : AutomationEditorAction
    data class DefinitionSelected(val typeKey: String) : AutomationEditorAction
    data class PickerFieldChanged(val fieldId: String, val value: String) : AutomationEditorAction
    data object SavePickerClicked : AutomationEditorAction
}

sealed interface AutomationEditorEvent {
    data object NavigateBackToRules : AutomationEditorEvent
    data class NavigateToDefinitionSelection(
        val section: RuleSection,
        val editingIndex: Int?
    ) : AutomationEditorEvent

    data class NavigateToDefinitionConfiguration(
        val section: RuleSection,
        val typeKey: String,
        val editingIndex: Int?
    ) : AutomationEditorEvent

    data object PopToDefinitionSelection : AutomationEditorEvent
    data object PopToEditor : AutomationEditorEvent
}
