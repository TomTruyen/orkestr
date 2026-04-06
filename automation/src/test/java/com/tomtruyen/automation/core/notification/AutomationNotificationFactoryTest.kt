package com.tomtruyen.automation.core.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import androidx.test.core.app.ApplicationProvider
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
internal class AutomationNotificationFactoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val factory = AutomationNotificationFactory(context)

    @Test
    fun runtimeChannel_matchesExpectedMetadata() {
        val channel = factory.runtimeChannel()

        assertEquals(AutomationNotificationFactory.RUNTIME_CHANNEL_ID, channel.id)
        assertEquals("Automation runtime", channel.name)
        assertEquals("Keeps Orkestr listening for automation triggers.", channel.description)
    }

    @Test
    fun actionChannel_matchesExpectedMetadata() {
        val channel = factory.actionChannel()

        assertEquals(AutomationNotificationFactory.ACTION_CHANNEL_ID, channel.id)
        assertEquals("Automation actions", channel.name)
        assertEquals("Notifications posted by automation actions.", channel.description)
    }

    @Test
    fun buildRuntimeNotification_whenLaunchIntentExists_setsContentIntent() {
        val launcherComponent =
            ComponentName(context.packageName, "com.tomtruyen.automation.TestLauncherActivity")
        val activityInfo = ActivityInfo().apply {
            packageName = launcherComponent.packageName
            name = launcherComponent.className
        }
        shadowOf(context.packageManager).apply {
            addOrUpdateActivity(activityInfo)
            addIntentFilterForActivity(
                launcherComponent,
                IntentFilter(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                },
            )
        }

        val notification = factory.buildRuntimeNotification()

        assertNotNull(notification.contentIntent)
    }

    @Test
    fun buildActionNotification_usesConfigContent() {
        val notification = factory.buildActionNotification(
            ShowNotificationActionConfig(title = "Title", message = "Message"),
        )

        assertEquals("Title", shadowOf(notification).contentTitle.toString())
        assertEquals("Message", shadowOf(notification).contentText.toString())
    }
}
