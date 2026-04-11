package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState

internal val packageChangedRegistry = AutomationDefinitionRegistry(
    triggers = emptyList(),
    constraints = emptyList(),
    actions = emptyList(),
)

private val packageChangedResolver = object : StringResolver {
    override fun resolve(stringRes: Int, vararg formatArgs: Any): String = "res:$stringRes" + formatArgs.joinToString(
        prefix = ":",
        separator = ",",
    ).takeIf(String::isNotBlank).orEmpty()
}

internal fun packageChangedCoordinator(
    state: () -> AutomationEditorUiState = { AutomationEditorUiState() },
    updateState: ((AutomationEditorUiState) -> AutomationEditorUiState) -> Unit = {},
    triggerEvent: (AutomationEditorEvent) -> Unit = {},
) = AutomationRuleEditorCustomFlowCoordinator(
    definitions = packageChangedRegistry,
    stringResolver = packageChangedResolver,
    state = state,
    updateState = updateState,
    triggerEvent = triggerEvent,
)
