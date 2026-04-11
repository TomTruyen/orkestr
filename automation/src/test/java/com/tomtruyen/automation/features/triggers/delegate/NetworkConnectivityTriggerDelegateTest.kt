package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.ManualAutomationEvent
import com.tomtruyen.automation.core.event.NetworkConnectivityEvent
import com.tomtruyen.automation.features.triggers.config.NetworkConnectivityTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class NetworkConnectivityTriggerDelegateTest {
    private val delegate = NetworkConnectivityTriggerDelegate()

    @Test
    fun matches_whenStateMatches_returnsTrue() {
        assertTrue(delegate.matches(NetworkConnectivityTriggerConfig(connected = true), NetworkConnectivityEvent(true)))
    }

    @Test
    fun matches_whenStateDoesNotMatch_returnsFalse() {
        assertFalse(
            delegate.matches(NetworkConnectivityTriggerConfig(connected = true), NetworkConnectivityEvent(false)),
        )
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(NetworkConnectivityTriggerConfig(), ManualAutomationEvent("rule")))
    }
}
