package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.NotificationReceivedEvent
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class NotificationReceivedTriggerDelegateTest {
    private val delegate = NotificationReceivedTriggerDelegate()

    @Test
    fun matches_whenPackageMatches_returnsTrue() {
        val config = NotificationReceivedTriggerConfig(packageName = "com.whatsapp")
        val event = NotificationReceivedEvent(packageName = "com.whatsapp", title = "Ping", message = "Hello")

        assertTrue(delegate.matches(config, event))
    }

    @Test
    fun matches_whenPackageDoesNotMatch_returnsFalse() {
        val config = NotificationReceivedTriggerConfig(packageName = "com.whatsapp")
        val event = NotificationReceivedEvent(packageName = "com.spotify.music", title = "Now playing", message = null)

        assertFalse(delegate.matches(config, event))
    }

    @Test
    fun matches_whenEventTypeDiffers_returnsFalse() {
        assertFalse(delegate.matches(NotificationReceivedTriggerConfig(), mockk<AutomationEvent>()))
    }
}
