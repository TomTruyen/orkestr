package com.tomtruyen.orkestr.features.logs.state

import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import java.util.Locale

internal fun reduceLogs(
    logs: List<AutomationLog>,
    query: String,
    sortOption: LogSortOption,
): List<AutomationLog> {
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
    AutomationLogSeverity.ERROR -> 4
    AutomationLogSeverity.WARNING -> 3
    AutomationLogSeverity.INFO -> 2
    AutomationLogSeverity.DEBUG -> 1
}
