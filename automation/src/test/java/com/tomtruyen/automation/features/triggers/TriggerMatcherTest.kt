package com.tomtruyen.automation.features.triggers

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.delegate.TriggerDelegate
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class TriggerMatcherTest {
    @MockK
    private lateinit var matchingDelegate: TriggerDelegate<BatteryChangedTriggerConfig>

    @MockK
    private lateinit var nonMatchingDelegate: TriggerDelegate<BatteryChangedTriggerConfig>

    private lateinit var trigger: BatteryChangedTriggerConfig
    private lateinit var event: AutomationEvent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        trigger = BatteryChangedTriggerConfig(state = BatteryChargeState.CHARGING)
        event = BatteryChangedEvent(
            level = 50,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
        )

        every { matchingDelegate.type } returns TriggerType.CHARGE_STATE
        every { nonMatchingDelegate.type } returns TriggerType.CHARGE_STATE
    }

    @Test
    fun matches_whenAnyTriggerMatches_returnsTrue() {
        every { matchingDelegate.matches(trigger, event) } returns true

        val matcher = TriggerMatcher(listOf(matchingDelegate))

        val result = matcher.matches(listOf(trigger), event)

        assertTrue(result)
        verify { matchingDelegate.matches(trigger, event) }
    }

    @Test
    fun matches_whenDelegateReturnsFalse_returnsFalse() {
        every { nonMatchingDelegate.matches(trigger, event) } returns false

        val matcher = TriggerMatcher(listOf(nonMatchingDelegate))

        val result = matcher.matches(listOf(trigger), event)

        assertFalse(result)
        verify { nonMatchingDelegate.matches(trigger, event) }
    }

    @Test
    fun matches_whenNoDelegateExistsForTriggerType_returnsFalse() {
        val matcher = TriggerMatcher(emptyList())

        val result = matcher.matches(listOf(trigger), event)

        assertFalse(result)
    }

    @Test
    fun matches_whenMultipleTriggersOnlyOneMatches_returnsTrue() {
        val firstTrigger = BatteryChangedTriggerConfig(state = BatteryChargeState.DISCHARGING)
        every { matchingDelegate.matches(firstTrigger, event) } returns false
        every { matchingDelegate.matches(trigger, event) } returns true

        val matcher = TriggerMatcher(listOf(matchingDelegate))

        val result = matcher.matches(listOf(firstTrigger, trigger), event)

        assertTrue(result)
        verify { matchingDelegate.matches(firstTrigger, event) }
        verify { matchingDelegate.matches(trigger, event) }
    }
}
