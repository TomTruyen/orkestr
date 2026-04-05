package com.tomtruyen.automation.features.actions.delegate

import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class LogMessageActionDelegateTest {
    private val delegate = LogMessageActionDelegate()

    @Test
    fun execute_printsConfiguredMessage() = runTest {
        val originalOut = System.out
        val output = ByteArrayOutputStream()
        System.setOut(PrintStream(output))

        try {
            delegate.execute(
                LogMessageActionConfig(message = "hello from test"),
                BatteryChangedEvent(
                    level = 10,
                    scale = 100,
                    chargeState = BatteryChargeState.CHARGING,
                    plugStatus = BatteryPlugStatus.AC,
                ),
            )
        } finally {
            System.setOut(originalOut)
        }

        assertTrue(output.toString().contains("hello from test"))
    }
}
