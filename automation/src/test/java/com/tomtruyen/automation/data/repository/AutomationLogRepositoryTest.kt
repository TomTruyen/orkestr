package com.tomtruyen.automation.data.repository

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import com.tomtruyen.automation.data.dao.AutomationLogDao
import com.tomtruyen.automation.data.entity.AutomationLogEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class AutomationLogRepositoryTest {
    @MockK
    private lateinit var dao: AutomationLogDao

    private lateinit var repository: AutomationLogRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AutomationLogRepositoryImpl(dao)
    }

    @Test
    fun observeLogs_normalizesRequestAndMapsPagingData() = runTest {
        val entity = AutomationLogEntity(
            id = 7L,
            timestampEpochMillis = 123L,
            severity = AutomationLogSeverity.ERROR.name,
            message = "Action failed",
            stackTrace = "IllegalStateException",
        )
        val pagingSourceFactory = listOf(entity).asPagingSourceFactory()
        every {
            dao.pagingSource(
                query = "boom",
                sort = AutomationLogSort.SEVERITY.name,
            )
        } answers {
            pagingSourceFactory()
        }

        val logs = repository.observeLogs(
            query = "  BOOM  ",
            sort = AutomationLogSort.SEVERITY,
        ).asSnapshot()

        assertEquals(
            listOf(
                AutomationLog(
                    id = 7L,
                    timestampEpochMillis = 123L,
                    severity = AutomationLogSeverity.ERROR,
                    message = "Action failed",
                    stackTrace = "IllegalStateException",
                ),
            ),
            logs,
        )
    }
}
