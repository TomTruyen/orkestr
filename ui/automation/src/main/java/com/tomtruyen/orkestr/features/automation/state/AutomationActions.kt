package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.features.actions.ActionExecutionMode

sealed interface AutomationRulesAction {
    data object CreateRuleClicked : AutomationRulesAction
    data class EditRuleClicked(val rule: AutomationRule) : AutomationRulesAction
    data class CopyRuleClicked(val rule: AutomationRule) : AutomationRulesAction
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
    data class RuleActionExecutionModeChanged(val executionMode: ActionExecutionMode) : AutomationEditorAction
    data object SaveRuleClicked : AutomationEditorAction
    data class AddNodeClicked(val section: RuleSection) : AutomationEditorAction
    data class EditNodeClicked(val section: RuleSection, val index: Int) : AutomationEditorAction
    data class DeleteNodeClicked(val section: RuleSection, val index: Int) : AutomationEditorAction
    data class SaveSectionAsGroupClicked(val section: RuleSection, val name: String) : AutomationEditorAction
    data class SaveSelectedNodesAsGroupClicked(val section: RuleSection, val indices: Set<Int>, val name: String) :
        AutomationEditorAction
    data class CreateConstraintConditionGroupClicked(val indices: Set<Int>) : AutomationEditorAction
    data class UpdateConstraintConditionGroupClicked(val groupIndex: Int, val indices: Set<Int>) :
        AutomationEditorAction
    data class CopyConstraintConditionGroupClicked(val groupIndex: Int) : AutomationEditorAction
    data class DeleteConstraintConditionGroupClicked(val groupIndex: Int) : AutomationEditorAction
    data class RemoveConstraintFromConditionGroupClicked(val groupIndex: Int, val constraintIndex: Int) :
        AutomationEditorAction
    data class AddConstraintToConditionGroupClicked(val groupIndex: Int) : AutomationEditorAction
    data class PickerQueryChanged(val query: String) : AutomationEditorAction
    data class DefinitionSelected(val typeKey: String) : AutomationEditorAction
    data class GroupSelected(val group: AutomationNodeGroup) : AutomationEditorAction
    data class PickerFieldChanged(val fieldId: String, val value: String) : AutomationEditorAction
    data class SaveDraftAsGroupClicked(val name: String) : AutomationEditorAction
    data object SavePickerClicked : AutomationEditorAction
}

sealed interface AutomationGroupsAction {
    data class DeleteGroupClicked(val group: AutomationNodeGroup) : AutomationGroupsAction
    data class UpdateGroupClicked(val group: AutomationNodeGroup) : AutomationGroupsAction
    data class CreateEmptyGroupClicked(val type: AutomationNodeGroupType, val name: String) : AutomationGroupsAction
}
