package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.ApplicationLifecycleEvent
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ApplicationLifecycleReceiverTest {
    @MockK private lateinit var service: AutomationRuntimeService

    @MockK private lateinit var logger: AutomationLogger

    @MockK private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { service.handleEvent(any()) } returns Unit
        every { logger.log(any()) } just runs
        every { logger.info(any()) } just runs
        every { context.applicationContext } returns context
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun startMonitoring_whenForegroundPackageChanges_emitsClosedAndLaunchedEvents() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val observedPackages = ArrayDeque(listOf("com.spotify.music", "com.whatsapp"))
        val receiver = ApplicationLifecycleReceiver(
            appContext = context,
            service = service,
            scope = scope,
            logger = logger,
            pollIntervalMillis = 1_000,
            currentForegroundPackageProvider = { observedPackages.removeFirstOrNull() },
        )

        receiver.startMonitoring()
        advanceTimeBy(1_000)
        runCurrent()
        receiver.stopMonitoring()
        advanceUntilIdle()

        coVerify {
            service.handleEvent(
                ApplicationLifecycleEvent(
                    packageName = "com.spotify.music",
                    transitionType = AppLifecycleTransitionType.CLOSED,
                ),
            )
        }
        coVerify {
            service.handleEvent(
                ApplicationLifecycleEvent(
                    packageName = "com.whatsapp",
                    transitionType = AppLifecycleTransitionType.LAUNCHED,
                ),
            )
        }
    }

    @Test
    fun pollForegroundPackage_whenProviderThrowsSecurityException_ignoresIt() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = ApplicationLifecycleReceiver(
            appContext = context,
            service = service,
            scope = scope,
            logger = logger,
            currentForegroundPackageProvider = { throw SecurityException("usage access revoked") },
        )

        try {
            receiver.pollForegroundPackage()
        } catch (_: SecurityException) {
            fail("SecurityException should be handled defensively")
        }

        coVerify(exactly = 0) { service.handleEvent(any()) }
    }
}
