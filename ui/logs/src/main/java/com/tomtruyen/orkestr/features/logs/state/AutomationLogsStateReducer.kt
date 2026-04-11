package com.tomtruyen.orkestr.features.logs.state

import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import java.util.Locale

private const val ERROR_SEVERITY_RANK = 4
private const val WARNING_SEVERITY_RANK = 3
private const val INFO_SEVERITY_RANK = 2
private const val DEBUG_SEVERITY_RANK = 1

internal fun reduceLogs(logs: List<AutomationLog>, query: String, sortOption: LogSortOption): List<AutomationLog> {
    val normalizedQuery = query.trim().lowercase(Locale.getDefault())
    return logs
        .asSequence()
        .filter { log ->
            normalizedQuery.isBlank() ||
                log.message.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                log.stackTrace.orEmpty().lowercase(Locale.getDefault()).contains(normalizedQuery)
        }
        .sortedWith(sortComparator(sortOption))
        .toList()
}

private fun sortComparator(sortOption: LogSortOption): Comparator<AutomationLog> = when (sortOption) {
    LogSortOption.NEWEST_FIRST -> compareByDescending<AutomationLog> { it.timestampEpochMillis }
        .thenByDescending { it.id }

    LogSortOption.OLDEST_FIRST -> compareBy<AutomationLog> { it.timestampEpochMillis }
        .thenBy { it.id }

    LogSortOption.SEVERITY -> compareByDescending<AutomationLog> { it.severity.rank() }
        .thenByDescending { it.timestampEpochMillis }
        .thenByDescending { it.id }
}

private fun AutomationLogSeverity.rank(): Int = when (this) {
    AutomationLogSeverity.ERROR -> ERROR_SEVERITY_RANK
    AutomationLogSeverity.WARNING -> WARNING_SEVERITY_RANK
    AutomationLogSeverity.INFO -> INFO_SEVERITY_RANK
    AutomationLogSeverity.DEBUG -> DEBUG_SEVERITY_RANK
}
