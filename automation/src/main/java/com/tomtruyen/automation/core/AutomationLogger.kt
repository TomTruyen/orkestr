package com.tomtruyen.automation.core

import android.util.Log
import com.tomtruyen.automation.data.dao.AutomationLogDao
import com.tomtruyen.automation.data.entity.AutomationLogEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

interface AutomationLogger {
    fun log(message: String)
    fun log(message: String, throwable: Throwable?)
}

class PersistingAutomationLogger(
    private val automationLogDao: AutomationLogDao,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: () -> Long = System::currentTimeMillis,
    private val logWriter: AutomationLogWriter = AndroidAutomationLogWriter,
) : AutomationLogger {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun log(message: String) {
        log(message, null)
    }

    override fun log(message: String, throwable: Throwable?) {
        logWriter.debug(TAG, message, throwable)
        scope.launch {
            automationLogDao.insert(
                AutomationLogEntity(
                    timestampEpochMillis = clock(),
                    message = message,
                    stackTrace = throwable?.stackTraceToString(),
                ),
            )
        }
    }

    companion object {
        private const val TAG = "AutomationLogger"
    }
}

interface AutomationLogWriter {
    fun debug(tag: String, message: String, throwable: Throwable?)
}

object AndroidAutomationLogWriter : AutomationLogWriter {
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (throwable == null) {
            Log.d(tag, message)
        } else {
            Log.d(tag, message, throwable)
        }
    }
}
