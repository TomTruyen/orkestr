package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.data.dao.AutomationNodeGroupDao
import com.tomtruyen.automation.data.entity.AutomationNodeGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AutomationNodeGroupRepository {
    fun observeGroups(): Flow<List<AutomationNodeGroup>>
    fun observeGroups(type: AutomationNodeGroupType): Flow<List<AutomationNodeGroup>>
    suspend fun upsertGroup(group: AutomationNodeGroup)
    suspend fun deleteGroup(id: String)
}

class AutomationNodeGroupRepositoryImpl(private val dao: AutomationNodeGroupDao) : AutomationNodeGroupRepository {
    override fun observeGroups(): Flow<List<AutomationNodeGroup>> = dao.observeAll().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun observeGroups(type: AutomationNodeGroupType): Flow<List<AutomationNodeGroup>> =
        dao.observeByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun upsertGroup(group: AutomationNodeGroup) {
        dao.upsert(group.toEntity())
    }

    override suspend fun deleteGroup(id: String) {
        dao.deleteById(id)
    }
}

private fun AutomationNodeGroupEntity.toDomain(): AutomationNodeGroup = AutomationNodeGroup(
    id = id,
    name = name,
    type = type.toAutomationNodeGroupType(),
    triggers = triggers,
    constraints = constraints,
    actions = actions,
)

private fun AutomationNodeGroup.toEntity(): AutomationNodeGroupEntity = AutomationNodeGroupEntity(
    id = id,
    name = name,
    type = type.name,
    triggers = triggers,
    constraints = constraints,
    actions = actions,
    updatedAtEpochMillis = System.currentTimeMillis(),
)

private fun String.toAutomationNodeGroupType(): AutomationNodeGroupType =
    runCatching { AutomationNodeGroupType.valueOf(this) }.getOrDefault(AutomationNodeGroupType.ACTION)
