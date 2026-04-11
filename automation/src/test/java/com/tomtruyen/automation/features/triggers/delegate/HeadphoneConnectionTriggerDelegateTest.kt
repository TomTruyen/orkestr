package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.HeadphoneConnectionEvent
import com.tomtruyen.automation.features.triggers.config.HeadphoneConnectionTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class HeadphoneConnectionTriggerDelegateTest {
    private val delegate = HeadphoneConnectionTriggerDelegate()

    @Test
    fun matches_whenStateMatches_returnsTrue() {
        assertTrue(delegate.matches(HeadphoneConnectionTriggerConfig(connected = true), HeadphoneConnectionEvent(true)))
    }

    @Test
    fun matches_whenStateDoesNotMatch_returnsFalse() {
        assertFalse(
            delegate.matches(HeadphoneConnectionTriggerConfig(connected = true), HeadphoneConnectionEvent(false)),
        )
    }
}
