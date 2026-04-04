package com.tomtruyen.orkestr.features.automation.viewmodel

import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesAction
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesUiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AutomationRulesViewModel(
    private val repository: AutomationRuleRepository
) : BaseViewModel<AutomationRulesUiState, AutomationRulesEvent, AutomationRulesAction>(
    initialState = AutomationRulesUiState()
) {
    init {
        repository.observeRules()
            .onEach { rules ->
                updateState { it.copy(rules = rules) }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AutomationRulesAction) {
        when (action) {
            AutomationRulesAction.CreateRuleClicked -> {
                triggerEvent(AutomationRulesEvent.NavigateToCreateRule)
            }

            is AutomationRulesAction.EditRuleClicked -> {
                triggerEvent(AutomationRulesEvent.NavigateToEditRule(action.rule))
            }

            is AutomationRulesAction.DeleteRuleClicked -> {
                launch {
                    repository.deleteRule(action.rule.id)
                }
            }

            is AutomationRulesAction.ToggleRuleEnabled -> {
                launch {
                    repository.updateEnabled(action.rule.id, action.enabled)
                }
            }
        }
    }
}
