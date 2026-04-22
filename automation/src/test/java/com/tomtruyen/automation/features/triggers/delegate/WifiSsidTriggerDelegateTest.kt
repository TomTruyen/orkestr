package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.WifiScanResultEvent
import com.tomtruyen.automation.core.model.WifiRangeTriggerType
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class WifiSsidTriggerDelegateTest {
    private val delegate = WifiSsidTriggerDelegate()

    @Test
    fun matches_whenVisibleSsidEntersRange_returnsTrue() {
        val config = WifiSsidTriggerConfig(ssid = "Office WiFi")
        val event = WifiScanResultEvent(
            visibleSsids = setOf("\"Office WiFi\""),
            connectedSsid = null,
            previousVisibleSsids = emptySet(),
            previousConnectedSsid = null,
        )

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenConnectedSsidEntersRange_returnsTrue() {
        val config = WifiSsidTriggerConfig(ssid = "Office WiFi")
        val event = WifiScanResultEvent(
            visibleSsids = emptySet(),
            connectedSsid = "\"Office WiFi\"",
            previousVisibleSsids = emptySet(),
            previousConnectedSsid = null,
        )

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenTriggerTypeIsOutOfRange_returnsTrueWhenNetworkLeavesRange() {
        val config = WifiSsidTriggerConfig(
            ssid = "Office WiFi",
            triggerType = WifiRangeTriggerType.OUT_OF_RANGE,
        )
        val event = WifiScanResultEvent(
            visibleSsids = setOf("Guest"),
            connectedSsid = null,
            previousVisibleSsids = setOf("Office WiFi"),
            previousConnectedSsid = null,
        )

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenStateRemainsInRange_returnsFalse() {
        val config = WifiSsidTriggerConfig(ssid = "Office WiFi")
        val event = WifiScanResultEvent(
            visibleSsids = setOf("Office WiFi", "Guest"),
            connectedSsid = null,
            previousVisibleSsids = setOf("Office WiFi"),
            previousConnectedSsid = null,
        )

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenNoPreviousSnapshotExists_returnsFalse() {
        val config = WifiSsidTriggerConfig(ssid = "Office WiFi")
        val event = WifiScanResultEvent(
            visibleSsids = setOf("Office WiFi"),
            connectedSsid = null,
        )

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(WifiSsidTriggerConfig(), mockk<AutomationEvent>()))
    }
}
