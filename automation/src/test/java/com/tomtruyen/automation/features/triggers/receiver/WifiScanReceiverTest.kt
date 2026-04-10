package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.WifiScanResultEvent
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
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class WifiScanReceiverTest {
    @MockK private lateinit var service: AutomationRuntimeService

    @MockK private lateinit var logger: AutomationLogger

    @MockK private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { service.handleEvent(any()) } returns Unit
        every { logger.log(any()) } just runs
        every { logger.debug(any()) } just runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onReceive_whenScanResultsArrive_forwardsEvent() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = WifiScanReceiver(
            service = service,
            scope = scope,
            logger = logger,
            visibleSsidsProvider = { setOf("Office WiFi", "Guest") },
            connectedSsidProvider = { "\"Office WiFi\"" },
        )

        receiver.onReceive(context, Intent(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        advanceUntilIdle()

        coVerify {
            service.handleEvent(
                WifiScanResultEvent(
                    visibleSsids = setOf("Office WiFi", "Guest"),
                    connectedSsid = "\"Office WiFi\"",
                ),
            )
        }
    }

    @Test
    fun factoryRegister_registersReceiver() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.registerReceiver(context, any(), any(), ContextCompat.RECEIVER_NOT_EXPORTED)
        } returns null

        val receiver = WifiScanReceiver.Factory.register(context, service, scope, logger)

        verify {
            ContextCompat.registerReceiver(
                context,
                receiver,
                match {
                    it.hasAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) &&
                        it.hasAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
    }

    @Test
    fun onReceive_whenWifiApisThrowSecurityException_emitsEmptySnapshot() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = WifiScanReceiver(
            service = service,
            scope = scope,
            logger = logger,
            visibleSsidsProvider = { throw SecurityException("blocked") },
            connectedSsidProvider = { throw SecurityException("blocked") },
        )

        try {
            receiver.onReceive(context, Intent(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            advanceUntilIdle()
        } catch (_: SecurityException) {
            fail("SecurityException should be handled defensively")
        }

        coVerify {
            service.handleEvent(
                WifiScanResultEvent(
                    visibleSsids = emptySet(),
                    connectedSsid = null,
                ),
            )
        }
    }
}
