package com.tomtruyen.automation.features.triggers.receiver

import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.HeadphoneConnectionEvent
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class HeadphoneConnectionReceiverTest {
    @MockK private lateinit var service: AutomationRuntimeService

    @MockK private lateinit var logger: AutomationLogger

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { service.handleEvent(any()) } returns Unit
        every { logger.info(any()) } just runs
    }

    @Test
    fun emitIfChanged_forwardsChangedStateOnly() = runTest {
        val connectedStates = ArrayDeque(listOf(true, false, false))
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = HeadphoneConnectionReceiver(service, scope, logger, null) {
            connectedStates.removeFirst()
        }

        receiver.emitIfChanged()
        receiver.emitIfChanged()
        advanceUntilIdle()

        coVerify(exactly = 1) { service.handleEvent(HeadphoneConnectionEvent(false)) }
    }
}
