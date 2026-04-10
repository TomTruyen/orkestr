package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.GeofenceTransitionEvent
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
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
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class GeofenceBroadcastReceiverTest {
    @MockK
    private lateinit var runtimeService: AutomationRuntimeService

    @MockK
    private lateinit var logger: AutomationLogger

    @MockK
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { runtimeService.handleEvent(any()) } returns Unit
        every { logger.log(any()) } just runs
        every { logger.debug(any()) } just runs
        every { logger.info(any()) } just runs
        every { logger.warning(any()) } just runs
        every { logger.error(any(), any()) } just runs
        startKoin {
            modules(
                module {
                    single<AutomationRuntimeService> { this@GeofenceBroadcastReceiverTest.runtimeService }
                    single<AutomationLogger> { this@GeofenceBroadcastReceiverTest.logger }
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
    fun onReceive_whenEventCannotBeParsed_logsAndStops() = runTest {
        mockkStatic(GeofencingEvent::class)
        every { GeofencingEvent.fromIntent(any()) } returns null

        GeofenceBroadcastReceiver().onReceive(context, Intent("geofence"))
        waitUntil {
            try {
                verify { logger.warning("Received geofence intent but could not parse GeofencingEvent") }
                true
            } catch (_: AssertionError) {
                false
            }
        }

        coVerify(exactly = 0) { runtimeService.handleEvent(any()) }
    }

    @Test
    fun onReceive_whenEventHasError_logsAndStops() = runTest {
        mockkStatic(GeofencingEvent::class)
        val event = mockk<GeofencingEvent>()
        every { GeofencingEvent.fromIntent(any()) } returns event
        every { event.hasError() } returns true
        every { event.errorCode } returns GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE

        GeofenceBroadcastReceiver().onReceive(context, Intent("geofence"))
        waitUntil {
            try {
                verify { logger.error(match { it.contains("Geofence transition error") }, null) }
                true
            } catch (_: AssertionError) {
                false
            }
        }

        coVerify(exactly = 0) { runtimeService.handleEvent(any()) }
    }

    @Test
    fun onReceive_whenTransitionIsValid_logsAndForwardsEvents() = runTest {
        mockkStatic(GeofencingEvent::class)
        val event = mockk<GeofencingEvent>()
        val triggeringGeofence = mockk<Geofence>()
        every { GeofencingEvent.fromIntent(any()) } returns event
        every { event.hasError() } returns false
        every { event.geofenceTransition } returns Geofence.GEOFENCE_TRANSITION_ENTER
        every { event.triggeringGeofences } returns listOf(triggeringGeofence)
        every { triggeringGeofence.requestId } returns "home"

        GeofenceBroadcastReceiver().onReceive(context, Intent("geofence"))
        waitUntil {
            try {
                coVerify {
                    runtimeService.handleEvent(
                        GeofenceTransitionEvent(
                            geofenceId = "home",
                            transitionType = GeofenceTransitionType.ENTER,
                        ),
                    )
                }
                verify { logger.info("Received geofence transition ENTER for home") }
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
