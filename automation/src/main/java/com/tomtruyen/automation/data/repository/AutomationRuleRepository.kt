package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.dao.AutomationRuleDao
import com.tomtruyen.automation.data.entity.AutomationRuleEntity
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AutomationRuleRepository {
    fun observeRules(): Flow<List<AutomationRule>>
    suspend fun getRule(id: String): AutomationRule?
    suspend fun getEnabledRules(): List<AutomationRule>
    suspend fun upsertRule(rule: AutomationRule)
    suspend fun deleteRule(id: String)
    suspend fun updateEnabled(id: String, enabled: Boolean)
}

class AutomationRuleRepositoryImpl(private val dao: AutomationRuleDao) : AutomationRuleRepository {
    override fun observeRules(): Flow<List<AutomationRule>> = dao.observeAll().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getRule(id: String): AutomationRule? = dao.getById(id)?.toDomain()

    override suspend fun getEnabledRules(): List<AutomationRule> = dao.getEnabled().map { it.toDomain() }

    override suspend fun upsertRule(rule: AutomationRule) {
        dao.upsert(rule.toEntity())
    }

    override suspend fun deleteRule(id: String) {
        dao.deleteById(id)
    }

    override suspend fun updateEnabled(id: String, enabled: Boolean) {
        dao.updateEnabled(
            id = id,
            enabled = enabled,
            updatedAtEpochMillis = System.currentTimeMillis(),
        )
    }
}

private fun AutomationRuleEntity.toDomain(): AutomationRule = AutomationRule(
    id = id,
    name = name,
    enabled = enabled,
    triggers = triggers,
    constraints = constraints,
    actions = actions,
    actionExecutionMode = actionExecutionMode.toActionExecutionMode(),
)

private fun AutomationRule.toEntity(): AutomationRuleEntity = AutomationRuleEntity(
    id = id,
    name = name,
    enabled = enabled,
    triggers = triggers,
    constraints = constraints,
    actions = actions,
    actionExecutionMode = actionExecutionMode.name,
    updatedAtEpochMillis = System.currentTimeMillis(),
)

private fun String.toActionExecutionMode(): ActionExecutionMode =
    runCatching { ActionExecutionMode.valueOf(this) }.getOrDefault(ActionExecutionMode.PARALLEL)
