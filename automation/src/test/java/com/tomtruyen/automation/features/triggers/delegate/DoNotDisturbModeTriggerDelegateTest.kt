package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.DoNotDisturbModeChangedEvent
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.features.triggers.config.DoNotDisturbModeTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DoNotDisturbModeTriggerDelegateTest {
    private val delegate = DoNotDisturbModeTriggerDelegate()

    @Test
    fun matches_whenModeMatches_returnsTrue() {
        assertTrue(
            delegate.matches(
                DoNotDisturbModeTriggerConfig(DoNotDisturbMode.PRIORITY_ONLY),
                DoNotDisturbModeChangedEvent(DoNotDisturbMode.PRIORITY_ONLY),
            ),
        )
    }

    @Test
    fun matches_whenModeDiffers_returnsFalse() {
        assertFalse(
            delegate.matches(
                DoNotDisturbModeTriggerConfig(DoNotDisturbMode.PRIORITY_ONLY),
                DoNotDisturbModeChangedEvent(DoNotDisturbMode.OFF),
            ),
        )
    }
}
