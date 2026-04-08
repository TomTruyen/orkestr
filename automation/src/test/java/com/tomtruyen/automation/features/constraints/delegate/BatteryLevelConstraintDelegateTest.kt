package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import android.os.BatteryManager
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class BatteryLevelConstraintDelegateTest {
    @Test
    fun evaluate_whenBatteryPercentMatchesConstraint_returnsTrue() = runTest {
        val delegate = delegateWithBatteryLevel(80)

        val result = delegate.evaluate(
            BatteryLevelConstraintConfig(
                operator = ComparisonOperator.GREATER_THAN_OR_EQUAL,
                value = 80,
            ),
            mockk<AutomationEvent>(),
        )

        assertTrue(result)
    }

    @Test
    fun evaluate_whenBatteryPercentDoesNotMatchConstraint_returnsFalse() = runTest {
        val delegate = delegateWithBatteryLevel(50)

        val result = delegate.evaluate(
            BatteryLevelConstraintConfig(
                operator = ComparisonOperator.LESS_THAN,
                value = 40,
            ),
            mockk<AutomationEvent>(),
        )

        assertFalse(result)
    }

    @Test
    fun evaluate_whenBatteryLevelUnavailable_returnsFalse() = runTest {
        val delegate = delegateWithBatteryLevel(Int.MIN_VALUE)

        val result = delegate.evaluate(BatteryLevelConstraintConfig(), mockk<AutomationEvent>())

        assertFalse(result)
    }

    private fun delegateWithBatteryLevel(level: Int): BatteryLevelConstraintDelegate {
        val batteryManager = mockk<BatteryManager>()
        every { batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) } returns level

        val context = mockk<Context>()
        every { context.applicationContext } returns context
        every { context.getSystemService(BatteryManager::class.java) } returns batteryManager

        return BatteryLevelConstraintDelegate(context)
    }
}
