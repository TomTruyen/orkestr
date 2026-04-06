package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.triggers.config.BatteryLevelTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class BatteryLevelTriggerDelegateTest {
    private val delegate = BatteryLevelTriggerDelegate()

    @Test
    fun matches_whenBatteryPercentageCrossesIntoMatch_returnsTrue() {
        val config = BatteryLevelTriggerConfig(operator = ComparisonOperator.LESS_THAN_OR_EQUAL, value = 20)
        val event = BatteryChangedEvent(
            level = 20,
            scale = 100,
            chargeState = BatteryChargeState.DISCHARGING,
            plugStatus = BatteryPlugStatus.UNPLUGGED,
            previousLevel = 21,
            previousScale = 100,
        )

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenBatteryPercentageAlreadyMatchedPreviously_returnsFalse() {
        val config = BatteryLevelTriggerConfig(operator = ComparisonOperator.LESS_THAN_OR_EQUAL, value = 20)
        val event = BatteryChangedEvent(
            level = 20,
            scale = 100,
            chargeState = BatteryChargeState.DISCHARGING,
            plugStatus = BatteryPlugStatus.UNPLUGGED,
            previousLevel = 20,
            previousScale = 100,
        )

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenBatteryPercentageDoesNotMatch_returnsFalse() {
        val config = BatteryLevelTriggerConfig(operator = ComparisonOperator.GREATER_THAN, value = 80)
        val event = BatteryChangedEvent(
            level = 20,
            scale = 100,
            chargeState = BatteryChargeState.DISCHARGING,
            plugStatus = BatteryPlugStatus.UNPLUGGED,
        )

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(BatteryLevelTriggerConfig(), mockk<AutomationEvent>()))
    }
}
