package com.tomtruyen.automation.features.actions.delegate

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.permission.NotificationPolicyAccessPermission
import com.tomtruyen.automation.features.actions.config.DoNotDisturbActionConfig
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
internal class DoNotDisturbActionDelegateTest {
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var notificationManager: NotificationManager

    private lateinit var delegate: DoNotDisturbActionDelegate

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(NotificationPolicyAccessPermission)

        every { context.getSystemService(NotificationManager::class.java) } returns notificationManager
        every { notificationManager.setInterruptionFilter(any()) } just runs
        every { notificationManager.isNotificationPolicyAccessGranted } returns true

        delegate = DoNotDisturbActionDelegate(context)
    }

    @Test
    fun execute_whenPermissionAndAccessAreGranted_setsInterruptionFilter() = runTest {
        every { NotificationPolicyAccessPermission.isGranted(context) } returns true

        delegate.execute(
            DoNotDisturbActionConfig(mode = DoNotDisturbMode.ALARMS_ONLY),
            batteryChangedEvent(),
        )

        verify { notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS) }
    }

    @Test
    fun execute_whenModeIsPriorityOnly_setsPriorityFilter() = runTest {
        every { NotificationPolicyAccessPermission.isGranted(context) } returns true

        delegate.execute(
            DoNotDisturbActionConfig(mode = DoNotDisturbMode.PRIORITY_ONLY),
            batteryChangedEvent(),
        )

        verify { notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY) }
    }

    @Test
    fun execute_whenModeIsTotalSilence_setsNoneFilter() = runTest {
        every { NotificationPolicyAccessPermission.isGranted(context) } returns true

        delegate.execute(
            DoNotDisturbActionConfig(mode = DoNotDisturbMode.TOTAL_SILENCE),
            batteryChangedEvent(),
        )

        verify { notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE) }
    }

    @Test
    fun execute_whenModeIsOff_setsAllFilter() = runTest {
        every { NotificationPolicyAccessPermission.isGranted(context) } returns true

        delegate.execute(
            DoNotDisturbActionConfig(mode = DoNotDisturbMode.OFF),
            batteryChangedEvent(),
        )

        verify { notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL) }
    }

    @Test
    fun execute_whenPermissionIsDenied_doesNothing() = runTest {
        every { NotificationPolicyAccessPermission.isGranted(context) } returns false

        delegate.execute(DoNotDisturbActionConfig(), batteryChangedEvent())

        verify(exactly = 0) { notificationManager.setInterruptionFilter(any()) }
    }

    @Test
    fun execute_whenPolicyAccessIsDenied_doesNothing() = runTest {
        every { NotificationPolicyAccessPermission.isGranted(context) } returns true
        every { notificationManager.isNotificationPolicyAccessGranted } returns false

        delegate.execute(DoNotDisturbActionConfig(), batteryChangedEvent())

        verify(exactly = 0) { notificationManager.setInterruptionFilter(any()) }
    }

    private fun batteryChangedEvent() = BatteryChangedEvent(
        level = 20,
        scale = 100,
        chargeState = BatteryChargeState.CHARGING,
        plugStatus = BatteryPlugStatus.AC,
    )
}
