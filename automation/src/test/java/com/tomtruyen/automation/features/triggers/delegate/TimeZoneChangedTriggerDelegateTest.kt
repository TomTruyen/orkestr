package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.ManualAutomationEvent
import com.tomtruyen.automation.core.event.TimeZoneChangedEvent
import com.tomtruyen.automation.features.triggers.config.TimeZoneChangedTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class TimeZoneChangedTriggerDelegateTest {
    private val delegate = TimeZoneChangedTriggerDelegate()

    @Test
    fun matches_whenTimeZoneEvent_returnsTrue() {
        assertTrue(delegate.matches(TimeZoneChangedTriggerConfig, TimeZoneChangedEvent("Europe/Brussels")))
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(TimeZoneChangedTriggerConfig, ManualAutomationEvent("rule")))
    }
}
