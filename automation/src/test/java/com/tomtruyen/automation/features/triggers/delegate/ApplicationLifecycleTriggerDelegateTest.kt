package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.ApplicationLifecycleEvent
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ApplicationLifecycleTriggerDelegateTest {
    private val delegate = ApplicationLifecycleTriggerDelegate()

    @Test
    fun matches_whenPackageAndStateMatch_returnsTrue() {
        val config = ApplicationLifecycleTriggerConfig(
            packageName = "com.spotify.music",
            transitionType = AppLifecycleTransitionType.LAUNCHED,
        )
        val event = ApplicationLifecycleEvent(
            packageName = "com.spotify.music",
            transitionType = AppLifecycleTransitionType.LAUNCHED,
        )

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenPackageDoesNotMatch_returnsFalse() {
        val config = ApplicationLifecycleTriggerConfig(
            packageName = "com.spotify.music",
            transitionType = AppLifecycleTransitionType.LAUNCHED,
        )
        val event = ApplicationLifecycleEvent(
            packageName = "com.whatsapp",
            transitionType = AppLifecycleTransitionType.LAUNCHED,
        )

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(ApplicationLifecycleTriggerConfig(), mockk<AutomationEvent>()))
    }
}
