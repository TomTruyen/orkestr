package com.tomtruyen.automation.features.constraints.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class BatteryLevelConstraintDelegateTest {
    private val delegate = BatteryLevelConstraintDelegate()

    @Test
    fun evaluate_whenBatteryPercentMatchesConstraint_returnsTrue() = runTest {
        val config = BatteryLevelConstraintConfig(
            operator = ComparisonOperator.GREATER_THAN_OR_EQUAL,
            value = 80,
        )
        val event = BatteryChangedEvent(
            level = 8,
            scale = 10,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
        )

        val result = delegate.evaluate(config, event)

        assertTrue(result)
    }

    @Test
    fun evaluate_whenBatteryPercentDoesNotMatchConstraint_returnsFalse() = runTest {
        val config = BatteryLevelConstraintConfig(
            operator = ComparisonOperator.LESS_THAN,
            value = 40,
        )
        val event = BatteryChangedEvent(
            level = 50,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
        )

        val result = delegate.evaluate(config, event)

        assertFalse(result)
    }

    @Test
    fun evaluate_whenScaleIsInvalid_returnsFalse() = runTest {
        val config = BatteryLevelConstraintConfig()
        val event = BatteryChangedEvent(
            level = 50,
            scale = 0,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
        )

        val result = delegate.evaluate(config, event)

        assertFalse(result)
    }

    @Test
    fun evaluate_whenEventIsDifferentType_returnsFalse() = runTest {
        val result = delegate.evaluate(BatteryLevelConstraintConfig(), mockk<AutomationEvent>())

        assertFalse(result)
    }
}
