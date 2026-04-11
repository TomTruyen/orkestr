package com.tomtruyen.automation.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import com.tomtruyen.automation.data.dao.AutomationLogDao
import com.tomtruyen.automation.data.entity.AutomationLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

interface AutomationLogRepository {
    fun observeLogs(query: String, sort: AutomationLogSort): Flow<PagingData<AutomationLog>>
}

class AutomationLogRepositoryImpl(private val dao: AutomationLogDao) : AutomationLogRepository {
    override fun observeLogs(query: String, sort: AutomationLogSort): Flow<PagingData<AutomationLog>> {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_LOG_PAGE_SIZE,
                prefetchDistance = LOG_PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                dao.pagingSource(query = normalizedQuery, sort = sort.name)
            },
        ).flow.map { pagingData ->
            pagingData.map(AutomationLogEntity::toDomain)
        }
    }

    private companion object {
        const val DEFAULT_LOG_PAGE_SIZE = 50
        const val LOG_PREFETCH_DISTANCE = 10
    }
}

enum class AutomationLogSort {
    NEWEST_FIRST,
    OLDEST_FIRST,
    SEVERITY,
}

private fun AutomationLogEntity.toDomain(): AutomationLog = AutomationLog(
    id = id,
    timestampEpochMillis = timestampEpochMillis,
    severity = AutomationLogSeverity.valueOf(severity),
    message = message,
    stackTrace = stackTrace,
)
