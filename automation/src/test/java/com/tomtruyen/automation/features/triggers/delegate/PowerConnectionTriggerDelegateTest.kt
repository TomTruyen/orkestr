package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.PowerConnectionEvent
import com.tomtruyen.automation.core.model.PowerConnectionState
import com.tomtruyen.automation.features.triggers.config.PowerConnectionTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class PowerConnectionTriggerDelegateTest {
    private val delegate = PowerConnectionTriggerDelegate()

    @Test
    fun matches_whenStateMatches_returnsTrue() {
        assertTrue(
            delegate.matches(
                PowerConnectionTriggerConfig(state = PowerConnectionState.CONNECTED),
                PowerConnectionEvent(PowerConnectionState.CONNECTED),
            ),
        )
    }

    @Test
    fun matches_whenStateDoesNotMatch_returnsFalse() {
        assertFalse(
            delegate.matches(
                PowerConnectionTriggerConfig(state = PowerConnectionState.CONNECTED),
                PowerConnectionEvent(PowerConnectionState.DISCONNECTED),
            ),
        )
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(PowerConnectionTriggerConfig(), mockk<AutomationEvent>()))
    }
}
