package com.tomtruyen.automation.data

import com.tomtruyen.automation.core.AutomationRule

interface AutomationRuleRepository {
    suspend fun getEnabledRules(): List<AutomationRule>
}

class AutomationRuleRepositoryImpl: AutomationRuleRepository {
    override suspend fun getEnabledRules(): List<AutomationRule> {
        // TODO: Implement
        return emptyList()
    }
}