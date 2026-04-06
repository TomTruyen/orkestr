package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.TimeBasedEvent
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class TimeBasedTriggerDelegateTest {
    private val delegate = TimeBasedTriggerDelegate()

    @Test
    fun matches_whenTimeAndDayMatch_returnsTrue() {
        val config = TimeBasedTriggerConfig(hour = 8, minute = 30, days = setOf(Weekday.MONDAY))
        val event = TimeBasedEvent(hour = 8, minute = 30, day = Weekday.MONDAY)

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenDayDoesNotMatch_returnsFalse() {
        val config = TimeBasedTriggerConfig(hour = 8, minute = 30, days = setOf(Weekday.MONDAY))
        val event = TimeBasedEvent(hour = 8, minute = 30, day = Weekday.TUESDAY)

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(TimeBasedTriggerConfig(), mockk<AutomationEvent>()))
    }
}
