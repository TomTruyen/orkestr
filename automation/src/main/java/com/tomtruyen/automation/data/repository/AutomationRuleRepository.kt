package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationRule
import kotlinx.coroutines.flow.Flow

interface AutomationRuleRepository {
    fun observeRules(): Flow<List<AutomationRule>>
    suspend fun getRule(id: String): AutomationRule?
    suspend fun getEnabledRules(): List<AutomationRule>
    suspend fun upsertRule(rule: AutomationRule)
    suspend fun deleteRule(id: String)
    suspend fun updateEnabled(id: String, enabled: Boolean)
}