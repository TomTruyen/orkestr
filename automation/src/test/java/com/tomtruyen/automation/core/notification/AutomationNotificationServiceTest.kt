package com.tomtruyen.automation.core.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Test

internal class AutomationNotificationServiceTest {
    private val context = mockk<Context>()
    private val factory = mockk<AutomationNotificationFactory>()
    private val notificationManager = mockk<NotificationManager>(relaxed = true)
    private val notificationManagerCompat = mockk<NotificationManagerCompat>()
    private val runtimeChannel = mockk<android.app.NotificationChannel>()
    private val actionChannel = mockk<android.app.NotificationChannel>()
    private val notification = mockk<Notification>()

    private val service = AutomationNotificationService(
        context = context,
        factory = factory,
        notificationManagerProvider = { notificationManager },
        notificationManagerCompatProvider = { notificationManagerCompat },
    )

    @Test
    fun ensureRuntimeChannel_registersRuntimeChannel() {
        every { factory.runtimeChannel() } returns runtimeChannel

        service.ensureRuntimeChannel()

        verify { notificationManager.createNotificationChannel(runtimeChannel) }
    }

    @Test
    fun ensureActionChannel_registersActionChannel() {
        every { factory.actionChannel() } returns actionChannel

        service.ensureActionChannel()

        verify { notificationManager.createNotificationChannel(actionChannel) }
    }

    @Test
    fun showActionNotification_buildsAndPostsNotification() {
        val config = ShowNotificationActionConfig(title = "Title", message = "Message")
        every { factory.buildActionNotification(config) } returns notification
        every { notificationManagerCompat.notify(any(), notification) } just runs

        service.showActionNotification(config)

        verify { factory.buildActionNotification(config) }
        verify { notificationManagerCompat.notify(any(), notification) }
    }
}
