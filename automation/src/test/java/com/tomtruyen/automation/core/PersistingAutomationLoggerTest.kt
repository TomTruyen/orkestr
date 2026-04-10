package com.tomtruyen.automation.core

import com.tomtruyen.automation.data.dao.AutomationLogDao
import com.tomtruyen.automation.data.entity.AutomationLogEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class PersistingAutomationLoggerTest {
    @Test
    fun log_persistsMessageAndWritesToLogcatBridge() = runTest {
        val dao = FakeAutomationLogDao()
        val writer = FakeAutomationLogWriter()
        val logger = PersistingAutomationLogger(
            automationLogDao = dao,
            dispatcher = StandardTestDispatcher(testScheduler),
            clock = { 1234L },
            logWriter = writer,
        )

        logger.log("receiver registered")
        advanceUntilIdle()

        assertEquals(
            listOf(
                LogInvocation(
                    severity = AutomationLogSeverity.INFO,
                    tag = "AutomationLogger",
                    message = "receiver registered",
                    throwable = null,
                ),
            ),
            writer.invocations,
        )
        assertEquals(
            listOf(
                AutomationLogEntity(
                    timestampEpochMillis = 1234L,
                    severity = AutomationLogSeverity.INFO.name,
                    message = "receiver registered",
                ),
            ),
            dao.insertedLogs,
        )
    }

    @Test
    fun log_withThrowable_persistsStackTrace() = runTest {
        val dao = FakeAutomationLogDao()
        val writer = FakeAutomationLogWriter()
        val logger = PersistingAutomationLogger(
            automationLogDao = dao,
            dispatcher = StandardTestDispatcher(testScheduler),
            clock = { 5678L },
            logWriter = writer,
        )
        val error = IllegalStateException("boom")

        logger.log("monitor failed", error)
        advanceUntilIdle()

        assertEquals(1, writer.invocations.size)
        assertEquals(error, writer.invocations.single().throwable)
        assertEquals(1, dao.insertedLogs.size)
        assertEquals(5678L, dao.insertedLogs.single().timestampEpochMillis)
        assertEquals("monitor failed", dao.insertedLogs.single().message)
        assertTrue(dao.insertedLogs.single().stackTrace.orEmpty().contains("IllegalStateException"))
    }

    private class FakeAutomationLogDao : AutomationLogDao {
        val insertedLogs = mutableListOf<AutomationLogEntity>()

        override fun observeAll(): Flow<List<AutomationLogEntity>> = flowOf(insertedLogs.toList())

        override suspend fun insert(log: AutomationLogEntity) {
            insertedLogs += log
        }
    }

    private class FakeAutomationLogWriter : AutomationLogWriter {
        val invocations = mutableListOf<LogInvocation>()

        override fun write(severity: AutomationLogSeverity, tag: String, message: String, throwable: Throwable?) {
            invocations += LogInvocation(severity = severity, tag = tag, message = message, throwable = throwable)
        }
    }

    private data class LogInvocation(
        val severity: AutomationLogSeverity,
        val tag: String,
        val message: String,
        val throwable: Throwable?,
    )
}
