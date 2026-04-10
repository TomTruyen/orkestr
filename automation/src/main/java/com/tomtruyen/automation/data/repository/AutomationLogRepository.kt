package com.tomtruyen.automation.data.repository

import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import com.tomtruyen.automation.data.dao.AutomationLogDao
import com.tomtruyen.automation.data.entity.AutomationLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AutomationLogRepository {
    fun observeLogs(): Flow<List<AutomationLog>>
}

class AutomationLogRepositoryImpl(private val dao: AutomationLogDao) : AutomationLogRepository {
    override fun observeLogs(): Flow<List<AutomationLog>> = dao.observeAll().map { entities ->
        entities.map(AutomationLogEntity::toDomain)
    }
}

private fun AutomationLogEntity.toDomain(): AutomationLog = AutomationLog(
    id = id,
    timestampEpochMillis = timestampEpochMillis,
    severity = AutomationLogSeverity.valueOf(severity),
    message = message,
    stackTrace = stackTrace,
)
