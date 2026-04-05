package com.tomtruyen.automation.core.permission

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
internal class NotificationPolicyAccessPermissionTest {
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun isGranted_whenNotificationPolicyAccessIsGranted_returnsTrue() {
        every { context.getSystemService(NotificationManager::class.java) } returns notificationManager
        every { notificationManager.isNotificationPolicyAccessGranted } returns true

        assertTrue(NotificationPolicyAccessPermission.isGranted(context))
    }

    @Test
    fun isGranted_whenNotificationPolicyAccessIsDenied_returnsFalse() {
        every { context.getSystemService(NotificationManager::class.java) } returns notificationManager
        every { notificationManager.isNotificationPolicyAccessGranted } returns false

        assertFalse(NotificationPolicyAccessPermission.isGranted(context))
    }

    @Test
    fun intentCreateIntent_opensNotificationPolicyAccessSettings() {
        val intent = NotificationPolicyAccessPermission.intent.createIntent(context)

        assertEquals(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS, intent.action)
    }
}
