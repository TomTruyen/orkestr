package com.tomtruyen.automation.features.triggers.receiver

import android.app.Notification
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.test.core.app.ApplicationProvider
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.NotificationReceivedEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class AutomationNotificationListenerServiceTest {
    @MockK private lateinit var runtimeService: AutomationRuntimeService

    @MockK private lateinit var logger: AutomationLogger

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { runtimeService.handleEvent(any()) } returns Unit
        every { logger.log(any()) } just runs
        startKoin {
            modules(
                module {
                    single<AutomationRuntimeService> { this@AutomationNotificationListenerServiceTest.runtimeService }
                    single<AutomationLogger> { this@AutomationNotificationListenerServiceTest.logger }
                },
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun onNotificationPosted_whenFromOwnPackage_ignoresNotification() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = Robolectric.buildService(AutomationNotificationListenerService::class.java).create().get()
        val notification = Notification.Builder(context, "channel")
            .setContentTitle("Title")
            .setContentText("Body")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        val sbn = mockk<StatusBarNotification>()
        every { sbn.packageName } returns context.packageName
        every { sbn.notification } returns notification

        service.onNotificationPosted(sbn)

        verify { logger.log(match { it.contains("Ignoring self-notification") }) }
        coVerify(exactly = 0) { runtimeService.handleEvent(any()) }
    }

    @Test
    fun onNotificationPosted_whenFromAnotherPackage_forwardsEvent() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = Robolectric.buildService(AutomationNotificationListenerService::class.java).create().get()
        val notification = Notification.Builder(context, "channel")
            .setContentTitle("Title")
            .setContentText("Body")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        val sbn = mockk<StatusBarNotification>()
        every { sbn.packageName } returns "com.whatsapp"
        every { sbn.notification } returns notification

        service.onNotificationPosted(sbn)
        waitUntil {
            try {
                coVerify {
                    runtimeService.handleEvent(
                        NotificationReceivedEvent(
                            packageName = "com.whatsapp",
                            title = "Title",
                            message = "Body",
                        ),
                    )
                }
                true
            } catch (_: AssertionError) {
                false
            }
        }
    }

    private fun waitUntil(timeoutMs: Long = 2_000, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!condition() && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
        check(condition()) { "Condition was not met within ${timeoutMs}ms" }
    }
}
