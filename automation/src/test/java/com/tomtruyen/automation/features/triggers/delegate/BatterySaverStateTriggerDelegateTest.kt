package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatterySaverStateChangedEvent
import com.tomtruyen.automation.features.triggers.config.BatterySaverStateTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class BatterySaverStateTriggerDelegateTest {
    private val delegate = BatterySaverStateTriggerDelegate()

    @Test
    fun matches_whenStateMatches_returnsTrue() {
        assertTrue(
            delegate.matches(BatterySaverStateTriggerConfig(enabled = true), BatterySaverStateChangedEvent(true)),
        )
    }

    @Test
    fun matches_whenStateDoesNotMatch_returnsFalse() {
        assertFalse(
            delegate.matches(BatterySaverStateTriggerConfig(enabled = true), BatterySaverStateChangedEvent(false)),
        )
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(BatterySaverStateTriggerConfig(), mockk<AutomationEvent>()))
    }
}
