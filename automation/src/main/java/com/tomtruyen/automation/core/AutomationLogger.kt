package com.tomtruyen.automation.core

import android.util.Log
import com.tomtruyen.automation.core.AutomationLogSeverity.DEBUG
import com.tomtruyen.automation.core.AutomationLogSeverity.ERROR
import com.tomtruyen.automation.core.AutomationLogSeverity.INFO
import com.tomtruyen.automation.core.AutomationLogSeverity.WARNING
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

    fun debug(message: String) = log(message)

    fun info(message: String) = log(message)

    fun warning(message: String) = log(message)

    fun error(message: String, throwable: Throwable? = null) = log(message, throwable)
}

class PersistingAutomationLogger(
    private val automationLogDao: AutomationLogDao,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: () -> Long = System::currentTimeMillis,
    private val logWriter: AutomationLogWriter = AndroidAutomationLogWriter,
) : AutomationLogger {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun log(message: String) = persist(INFO, message, null)

    override fun log(message: String, throwable: Throwable?) {
        if (throwable == null) {
            persist(INFO, message, null)
        } else {
            persist(ERROR, message, throwable)
        }
    }

    override fun debug(message: String) = persist(DEBUG, message, null)

    override fun info(message: String) = persist(INFO, message, null)

    override fun warning(message: String) = persist(WARNING, message, null)

    override fun error(message: String, throwable: Throwable?) = persist(ERROR, message, throwable)

    private fun persist(severity: AutomationLogSeverity, message: String, throwable: Throwable?) {
        logWriter.write(severity = severity, tag = TAG, message = message, throwable = throwable)
        scope.launch {
            automationLogDao.insert(
                AutomationLogEntity(
                    timestampEpochMillis = clock(),
                    severity = severity.name,
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
    fun write(severity: AutomationLogSeverity, tag: String, message: String, throwable: Throwable?)
}

object AndroidAutomationLogWriter : AutomationLogWriter {
    override fun write(severity: AutomationLogSeverity, tag: String, message: String, throwable: Throwable?) {
        when (severity) {
            DEBUG -> if (throwable == null) Log.d(tag, message) else Log.d(tag, message, throwable)
            INFO -> if (throwable == null) Log.i(tag, message) else Log.i(tag, message, throwable)
            WARNING -> if (throwable == null) Log.w(tag, message) else Log.w(tag, message, throwable)
            ERROR -> if (throwable == null) Log.e(tag, message) else Log.e(tag, message, throwable)
        }
    }
}
