package com.tomtruyen.automation.features.triggers.receiver

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.DoNotDisturbModeChangedEvent
import com.tomtruyen.automation.core.model.DoNotDisturbMode
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
internal class DoNotDisturbModeReceiverTest {
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
    fun onReceive_forwardsKnownMode() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = DoNotDisturbModeReceiver(service, scope, logger) {
            DoNotDisturbMode.ALARMS_ONLY
        }

        receiver.onReceive(context, Intent(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED))
        advanceUntilIdle()

        coVerify { service.handleEvent(DoNotDisturbModeChangedEvent(DoNotDisturbMode.ALARMS_ONLY)) }
    }
}
