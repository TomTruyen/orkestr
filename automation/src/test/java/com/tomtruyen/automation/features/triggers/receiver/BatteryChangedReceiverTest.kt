package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class BatteryChangedReceiverTest {
    @MockK
    private lateinit var service: AutomationRuntimeService

    @MockK
    private lateinit var logger: AutomationLogger

    @MockK
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { service.handleEvent(any()) } returns Unit
        every { logger.log(any()) } just runs
        every { logger.debug(any()) } just runs
        every { logger.info(any()) } just runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onReceive_whenIntentActionIsDifferent_ignoresEvent() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = BatteryChangedReceiver(service, scope, logger)

        receiver.onReceive(context, Intent(Intent.ACTION_SCREEN_ON))
        advanceUntilIdle()

        coVerify(exactly = 0) { service.handleEvent(any()) }
        verify(exactly = 0) { logger.log(any()) }
    }

    @Test
    fun onReceive_whenBatteryIntentIsReceived_logsAndForwardsEvent() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = BatteryChangedReceiver(service, scope, logger)
        val baselineIntent = Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_LEVEL, 49)
            putExtra(BatteryManager.EXTRA_SCALE, 100)
            putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_DISCHARGING)
            putExtra(BatteryManager.EXTRA_PLUGGED, 0)
        }
        val intent = Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_LEVEL, 50)
            putExtra(BatteryManager.EXTRA_SCALE, 100)
            putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING)
            putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_AC)
        }

        receiver.onReceive(context, baselineIntent)
        receiver.onReceive(context, intent)
        advanceUntilIdle()

        verify { logger.debug(match { it.contains("Ignoring initial battery snapshot") }) }
        verify { logger.info(match { it.contains("Received battery change event") }) }
        coVerify {
            service.handleEvent(
                BatteryChangedEvent(
                    level = 50,
                    scale = 100,
                    chargeState = BatteryChargeState.CHARGING,
                    plugStatus = BatteryPlugStatus.AC,
                    previousLevel = 49,
                    previousScale = 100,
                    previousChargeState = BatteryChargeState.DISCHARGING,
                    previousPlugStatus = BatteryPlugStatus.UNPLUGGED,
                ),
            )
        }
    }

    @Test
    fun onReceive_whenFirstBatteryIntentIsReceived_onlyStoresBaseline() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val receiver = BatteryChangedReceiver(service, scope, logger)
        val intent = Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_LEVEL, 50)
            putExtra(BatteryManager.EXTRA_SCALE, 100)
            putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING)
            putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_AC)
        }

        receiver.onReceive(context, intent)
        advanceUntilIdle()

        verify { logger.debug(match { it.contains("Ignoring initial battery snapshot") }) }
        coVerify(exactly = 0) { service.handleEvent(any()) }
    }

    @Test
    fun factoryRegister_registersBatteryChangedReceiver() = runTest {
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.registerReceiver(
                context,
                any(),
                any(),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        } returns null

        val receiver = BatteryChangedReceiver.Factory.register(context, service, scope, logger)

        verify {
            ContextCompat.registerReceiver(
                context,
                receiver,
                match { it.hasAction(Intent.ACTION_BATTERY_CHANGED) },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
    }
}
