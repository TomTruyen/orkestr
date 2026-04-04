package com.tomtruyen.orkestr.features.automation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AutomationRulesViewModel(
    private val repository: AutomationRuleRepository
) : ViewModel() {
    val rules: StateFlow<List<AutomationRule>> = repository.observeRules().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun toggleRuleEnabled(rule: AutomationRule, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateEnabled(rule.id, enabled)
        }
    }

    fun deleteRule(rule: AutomationRule) {
        viewModelScope.launch {
            repository.deleteRule(rule.id)
        }
    }
}
