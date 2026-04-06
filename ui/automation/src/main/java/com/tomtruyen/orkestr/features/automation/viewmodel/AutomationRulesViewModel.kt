package com.tomtruyen.orkestr.features.automation.viewmodel

import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesAction
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesUiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AutomationRulesViewModel(
    private val repository: AutomationRuleRepository,
    private val runtimeService: AutomationRuntimeService,
) : BaseViewModel<AutomationRulesUiState, AutomationRulesEvent, AutomationRulesAction>(
    initialState = AutomationRulesUiState(),
) {
    init {
        observeAutomationRules()
    }

    private fun observeAutomationRules() = repository.observeRules()
        .onEach { rules ->
            updateState { it.copy(rules = rules) }
        }.launchIn(viewModelScope)

    private fun deleteRule(id: String) = launch {
        repository.deleteRule(id)
    }

    private fun toggleRuleEnabled(id: String, enabled: Boolean) = launch {
        repository.updateEnabled(id, enabled)
    }

    private fun runRuleNow(rule: com.tomtruyen.automation.core.AutomationRule) = launch {
        runtimeService.runRuleNow(rule)
    }

    override fun onAction(action: AutomationRulesAction) {
        when (action) {
            AutomationRulesAction.CreateRuleClicked -> {
                triggerEvent(AutomationRulesEvent.NavigateToCreateRule)
            }

            is AutomationRulesAction.EditRuleClicked -> {
                triggerEvent(AutomationRulesEvent.NavigateToEditRule(action.rule))
            }

            is AutomationRulesAction.DeleteRuleClicked -> deleteRule(action.rule.id)

            is AutomationRulesAction.ToggleRuleEnabled -> toggleRuleEnabled(
                id = action.rule.id,
                enabled = action.enabled,
            )

            is AutomationRulesAction.RunRuleNowClicked -> runRuleNow(action.rule)
        }
    }
}
