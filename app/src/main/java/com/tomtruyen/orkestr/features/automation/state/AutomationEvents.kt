package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationRule

sealed interface AutomationRulesEvent {
    data object NavigateToCreateRule : AutomationRulesEvent
    data class NavigateToEditRule(val rule: AutomationRule) : AutomationRulesEvent
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
