package com.tomtruyen.automation.features.actions.delegate

import com.tomtruyen.automation.core.AutomationLogSeverity
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageSeverity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

internal class LogMessageActionDelegateTest {
    private val delegate = LogMessageActionDelegate()

    @Test
    fun execute_logsConfiguredMessageWithSeverity() = runTest {
        stopKoin()
        val logger = FakeAutomationLogger()
        startKoin {
            modules(
                module {
                    single<AutomationLogger> { logger }
                },
            )
        }

        try {
            delegate.execute(
                LogMessageActionConfig(
                    message = "hello from test",
                    severity = LogMessageSeverity.WARNING,
                ),
                BatteryChangedEvent(
                    level = 10,
                    scale = 100,
                    chargeState = BatteryChargeState.CHARGING,
                    plugStatus = BatteryPlugStatus.AC,
                ),
            )
        } finally {
            stopKoin()
        }

        assertEquals(
            listOf(
                LoggedMessage(
                    severity = AutomationLogSeverity.WARNING,
                    message = "hello from test",
                ),
            ),
            logger.messages,
        )
    }
}

private class FakeAutomationLogger : AutomationLogger {
    val messages = mutableListOf<LoggedMessage>()

    override fun log(message: String) {
        messages += LoggedMessage(
            severity = AutomationLogSeverity.INFO,
            message = message,
        )
    }

    override fun log(message: String, throwable: Throwable?) {
        messages += LoggedMessage(
            severity = AutomationLogSeverity.ERROR,
            message = message,
        )
    }

    override fun debug(message: String) {
        messages += LoggedMessage(AutomationLogSeverity.DEBUG, message)
    }

    override fun info(message: String) {
        messages += LoggedMessage(AutomationLogSeverity.INFO, message)
    }

    override fun warning(message: String) {
        messages += LoggedMessage(AutomationLogSeverity.WARNING, message)
    }

    override fun error(message: String, throwable: Throwable?) {
        messages += LoggedMessage(AutomationLogSeverity.ERROR, message)
    }
}

private data class LoggedMessage(val severity: AutomationLogSeverity, val message: String)
