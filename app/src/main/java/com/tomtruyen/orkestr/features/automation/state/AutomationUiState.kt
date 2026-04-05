package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationRule

data class AutomationRulesUiState(val rules: List<AutomationRule> = emptyList())

data class AutomationEditorUiState(
    val editorState: RuleEditorState? = null,
    val pickerState: DefinitionPickerState? = null,
)
