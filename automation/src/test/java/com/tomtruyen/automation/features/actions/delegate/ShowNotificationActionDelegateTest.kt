package com.tomtruyen.automation.features.actions.delegate

import android.os.Build
import android.content.Context
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import com.tomtruyen.automation.core.permission.PostNotificationPermission
import com.tomtruyen.automation.features.actions.config.ShowNotificationActionConfig
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.core.app.NotificationManagerCompat
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
internal class ShowNotificationActionDelegateTest {
    @MockK
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    private lateinit var context: Context
    private lateinit var delegate: ShowNotificationActionDelegate

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(PostNotificationPermission)
        mockkStatic(NotificationManagerCompat::class)

        context = ApplicationProvider.getApplicationContext()
        every { NotificationManagerCompat.from(context) } returns notificationManagerCompat
        every { notificationManagerCompat.notify(any(), any()) } returns Unit

        delegate = ShowNotificationActionDelegate(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun execute_whenPermissionIsGranted_postsNotification() = runTest {
        every { PostNotificationPermission.isGranted(context) } returns true

        delegate.execute(
            ShowNotificationActionConfig(title = "Title", message = "Message"),
            batteryChangedEvent()
        )

        verify { notificationManagerCompat.notify(any(), any()) }
    }

    @Test
    fun execute_whenPermissionIsDenied_doesNothing() = runTest {
        every { PostNotificationPermission.isGranted(context) } returns false

        delegate.execute(ShowNotificationActionConfig(), batteryChangedEvent())

        verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
    }

    private fun batteryChangedEvent() = BatteryChangedEvent(
        level = 20,
        scale = 100,
        chargeState = BatteryChargeState.CHARGING,
        plugStatus = BatteryPlugStatus.AC
    )
}
