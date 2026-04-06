package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ChargeStateTriggerDelegateTest {
    private val delegate = ChargeStateTriggerDelegate()

    @Test
    fun matches_whenEventEntersConfiguredChargeState_returnsTrue() {
        val config = BatteryChangedTriggerConfig(state = BatteryChargeState.CHARGING)
        val event = BatteryChangedEvent(
            level = 20,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
            previousChargeState = BatteryChargeState.DISCHARGING,
        )

        val result = delegate.matches(config, event)

        assertTrue(result)
    }

    @Test
    fun matches_whenEventRemainsInConfiguredChargeState_returnsFalse() {
        val config = BatteryChangedTriggerConfig(state = BatteryChargeState.CHARGING)
        val event = BatteryChangedEvent(
            level = 20,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
            previousChargeState = BatteryChargeState.CHARGING,
        )

        val result = delegate.matches(config, event)

        assertFalse(result)
    }

    @Test
    fun matches_whenEventChargeStateDoesNotMatchConfig_returnsFalse() {
        val config = BatteryChangedTriggerConfig(state = BatteryChargeState.FULL)
        val event = BatteryChangedEvent(
            level = 20,
            scale = 100,
            chargeState = BatteryChargeState.CHARGING,
            plugStatus = BatteryPlugStatus.AC,
            previousChargeState = BatteryChargeState.DISCHARGING,
        )

        val result = delegate.matches(config, event)

        assertFalse(result)
    }

    @Test
    fun matches_whenEventIsDifferentType_returnsFalse() {
        val config = BatteryChangedTriggerConfig()
        val event = mockk<AutomationEvent>()

        val result = delegate.matches(config, event)

        assertFalse(result)
    }
}
