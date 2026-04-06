package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.TimeBasedEvent
import com.tomtruyen.automation.core.model.Weekday
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class TimeTickReceiverTest {
    @MockK private lateinit var service: AutomationRuntimeService

    @MockK private lateinit var logger: AutomationLogger

    @MockK private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { service.handleEvent(any()) } returns Unit
        every { logger.log(any()) } just runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onReceive_whenIntentActionIsDifferent_ignoresEvent() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = TimeTickReceiver(service, scope, logger) { LocalDateTime.of(2026, 4, 6, 8, 30) }

        receiver.onReceive(context, Intent(Intent.ACTION_SCREEN_ON))
        advanceUntilIdle()

        coVerify(exactly = 0) { service.handleEvent(any()) }
    }

    @Test
    fun onReceive_whenTimeTickReceived_forwardsEvent() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = TimeTickReceiver(service, scope, logger) { LocalDateTime.of(2026, 4, 6, 8, 30) }

        receiver.onReceive(context, Intent(Intent.ACTION_TIME_TICK))
        advanceUntilIdle()

        coVerify {
            service.handleEvent(TimeBasedEvent(hour = 8, minute = 30, day = Weekday.MONDAY))
        }
    }

    @Test
    fun factoryRegister_registersReceiver() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.registerReceiver(context, any(), any(), ContextCompat.RECEIVER_NOT_EXPORTED)
        } returns null

        val receiver = TimeTickReceiver.Factory.register(context, service, scope, logger)

        verify {
            ContextCompat.registerReceiver(
                context,
                receiver,
                match { it.hasAction(Intent.ACTION_TIME_TICK) && it.hasAction(Intent.ACTION_TIME_CHANGED) },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
    }
}
