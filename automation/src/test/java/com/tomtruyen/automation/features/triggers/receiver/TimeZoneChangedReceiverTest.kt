package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.TimeZoneChangedEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class TimeZoneChangedReceiverTest {
    @MockK private lateinit var service: AutomationRuntimeService

    @MockK private lateinit var logger: AutomationLogger

    @MockK private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { service.handleEvent(any()) } returns Unit
        every { logger.info(any()) } just runs
    }

    @Test
    fun onReceive_forwardsCurrentTimeZone() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = TimeZoneChangedReceiver(service, scope, logger) { "Europe/Brussels" }

        receiver.onReceive(context, Intent(Intent.ACTION_TIMEZONE_CHANGED))
        advanceUntilIdle()

        coVerify { service.handleEvent(TimeZoneChangedEvent("Europe/Brussels")) }
    }
}
