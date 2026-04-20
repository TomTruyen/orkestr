package com.tomtruyen.automation.features.constraints

import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class ConstraintEvaluatorTest {
    @MockK
    private lateinit var delegate: ConstraintDelegate<BatteryLevelConstraintConfig>

    private lateinit var constraint: BatteryLevelConstraintConfig
    private lateinit var event: AutomationEvent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        constraint = BatteryLevelConstraintConfig(
            operator = ComparisonOperator.GREATER_THAN,
            value = 20,
        )
        event = BatteryChangedEvent(
            level = 50,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
        )

        coEvery { delegate.type } returns ConstraintType.BATTERY_LEVEL
    }

    @Test
    fun evaluateAll_whenConstraintListIsEmpty_returnsTrue() = runTest {
        val evaluator = ConstraintEvaluator(listOf(delegate))

        val result = evaluator.evaluateAll(emptyList(), event)

        assertTrue(result)
        coVerify(exactly = 0) { delegate.evaluate(any(), any()) }
    }

    @Test
    fun evaluateAll_whenAllConstraintsPass_returnsTrue() = runTest {
        coEvery { delegate.evaluate(constraint, event) } returns true
        val evaluator = ConstraintEvaluator(listOf(delegate))

        val result = evaluator.evaluateAll(listOf(constraint), event)

        assertTrue(result)
        coVerify { delegate.evaluate(constraint, event) }
    }

    @Test
    fun evaluateAll_whenAnyConstraintFails_returnsFalse() = runTest {
        val secondConstraint = constraint.copy(value = 90)
        coEvery { delegate.evaluate(constraint, event) } returns true
        coEvery { delegate.evaluate(secondConstraint, event) } returns false
        val evaluator = ConstraintEvaluator(listOf(delegate))

        val result = evaluator.evaluateAll(listOf(constraint, secondConstraint), event)

        assertFalse(result)
        coVerify { delegate.evaluate(constraint, event) }
        coVerify { delegate.evaluate(secondConstraint, event) }
    }

    @Test
    fun evaluateAll_whenNoDelegateExistsForConstraintType_returnsFalse() = runTest {
        val evaluator = ConstraintEvaluator(emptyList())

        val result = evaluator.evaluateAll(listOf(constraint), event)

        assertFalse(result)
    }

    @Test
    fun evaluateGroups_whenAnyGroupPasses_returnsTrue() = runTest {
        val failingConstraint = constraint.copy(value = 90)
        coEvery { delegate.evaluate(failingConstraint, event) } returns false
        coEvery { delegate.evaluate(constraint, event) } returns true
        val evaluator = ConstraintEvaluator(listOf(delegate))

        val result = evaluator.evaluateGroups(
            listOf(
                ConstraintGroup(listOf(failingConstraint)),
                ConstraintGroup(listOf(constraint)),
            ),
            event,
        )

        assertTrue(result)
    }

    @Test
    fun evaluateGroups_whenEveryGroupFails_returnsFalse() = runTest {
        val secondConstraint = constraint.copy(value = 90)
        coEvery { delegate.evaluate(constraint, event) } returns false
        coEvery { delegate.evaluate(secondConstraint, event) } returns false
        val evaluator = ConstraintEvaluator(listOf(delegate))

        val result = evaluator.evaluateGroups(
            listOf(
                ConstraintGroup(listOf(constraint)),
                ConstraintGroup(listOf(secondConstraint)),
            ),
            event,
        )

        assertFalse(result)
    }
}
