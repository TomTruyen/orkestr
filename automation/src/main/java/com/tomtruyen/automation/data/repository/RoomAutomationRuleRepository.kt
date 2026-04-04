package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.data.dao.AutomationRuleDao
import com.tomtruyen.automation.data.entity.AutomationRuleEntity
import com.tomtruyen.automation.data.local.AutomationJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomAutomationRuleRepository(
    private val dao: AutomationRuleDao
) : AutomationRuleRepository {
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
            updatedAtEpochMillis = System.currentTimeMillis()
        )
    }
}

private fun AutomationRuleEntity.toDomain(): AutomationRule = AutomationRule(
    id = id,
    name = name,
    enabled = enabled,
    triggers = AutomationJson.decodeTriggers(triggersJson),
    constraints = AutomationJson.decodeConstraints(constraintsJson),
    actions = AutomationJson.decodeActions(actionsJson)
)

private fun AutomationRule.toEntity(): AutomationRuleEntity = AutomationRuleEntity(
    id = id,
    name = name,
    enabled = enabled,
    triggersJson = AutomationJson.encodeTriggers(triggers),
    constraintsJson = AutomationJson.encodeConstraints(constraints),
    actionsJson = AutomationJson.encodeActions(actions),
    updatedAtEpochMillis = System.currentTimeMillis()
)
