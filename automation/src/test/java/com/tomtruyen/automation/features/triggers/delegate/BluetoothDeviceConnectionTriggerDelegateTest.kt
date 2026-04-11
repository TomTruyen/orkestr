package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.BluetoothDeviceConnectionEvent
import com.tomtruyen.automation.features.triggers.config.BluetoothDeviceConnectionTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class BluetoothDeviceConnectionTriggerDelegateTest {
    private val delegate = BluetoothDeviceConnectionTriggerDelegate()

    @Test
    fun matches_whenStateMatches_returnsTrue() {
        assertTrue(
            delegate.matches(
                BluetoothDeviceConnectionTriggerConfig(connected = false),
                BluetoothDeviceConnectionEvent(false),
            ),
        )
    }

    @Test
    fun matches_whenStateDoesNotMatch_returnsFalse() {
        assertFalse(
            delegate.matches(
                BluetoothDeviceConnectionTriggerConfig(connected = false),
                BluetoothDeviceConnectionEvent(true),
            ),
        )
    }
}
