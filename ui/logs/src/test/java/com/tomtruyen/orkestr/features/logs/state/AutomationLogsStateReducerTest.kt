package com.tomtruyen.orkestr.features.logs.state

import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationLogsStateReducerTest {
    @Test
    fun reduceLogs_filtersByMessageAndStackTrace() {
        val logs = listOf(
            AutomationLog(
                id = 1,
                timestampEpochMillis = 300L,
                severity = AutomationLogSeverity.INFO,
                message = "Received notification from com.whatsapp",
            ),
            AutomationLog(
                id = 2,
                timestampEpochMillis = 200L,
                severity = AutomationLogSeverity.ERROR,
                message = "Action failed",
                stackTrace = "IllegalStateException: boom",
            ),
        )

        val filtered = reduceLogs(
            logs = logs,
            query = "boom",
            sortOption = LogSortOption.NEWEST_FIRST,
        )

        assertEquals(listOf(2L), filtered.map(AutomationLog::id))
    }

    @Test
    fun reduceLogs_sortsBySeverityThenRecency() {
        val logs = listOf(
            AutomationLog(
                id = 1,
                timestampEpochMillis = 300L,
                severity = AutomationLogSeverity.INFO,
                message = "Info",
            ),
            AutomationLog(
                id = 2,
                timestampEpochMillis = 100L,
                severity = AutomationLogSeverity.ERROR,
                message = "Older error",
            ),
            AutomationLog(
                id = 3,
                timestampEpochMillis = 200L,
                severity = AutomationLogSeverity.ERROR,
                message = "Newer error",
            ),
        )

        val sorted = reduceLogs(
            logs = logs,
            query = "",
            sortOption = LogSortOption.SEVERITY,
        )

        assertEquals(listOf(3L, 2L, 1L), sorted.map(AutomationLog::id))
    }
}
