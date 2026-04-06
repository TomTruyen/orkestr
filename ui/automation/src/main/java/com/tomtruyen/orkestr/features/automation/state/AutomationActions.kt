package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationRule

sealed interface AutomationRulesAction {
    data object CreateRuleClicked : AutomationRulesAction
    data class EditRuleClicked(val rule: AutomationRule) : AutomationRulesAction
    data class DeleteRuleClicked(val rule: AutomationRule) : AutomationRulesAction
    data class ToggleRuleEnabled(val rule: AutomationRule, val enabled: Boolean) : AutomationRulesAction
    data class RunRuleNowClicked(val rule: AutomationRule) : AutomationRulesAction
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
