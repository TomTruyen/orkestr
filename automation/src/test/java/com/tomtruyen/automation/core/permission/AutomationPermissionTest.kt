package com.tomtruyen.automation.core.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.permission.PermissionIntent.AppSettings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
internal class AutomationPermissionTest {
    @MockK
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun runtimeIsGranted_whenPermissionIsGranted_returnsTrue() {
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_GRANTED

        assertTrue(PostNotificationPermission.isGranted(context))
    }

    @Test
    fun runtimeIsGranted_whenPermissionIsDenied_returnsFalse() {
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_DENIED

        assertFalse(PostNotificationPermission.isGranted(context))
    }

    @Test
    fun appSettingsCreateIntent_buildsPackageSettingsIntent() {
        every { context.packageName } returns "com.tomtruyen.automation.test"

        val intent = AppSettings.createIntent(context)

        assertEquals("android.settings.APPLICATION_DETAILS_SETTINGS", intent.action)
        assertEquals("package:com.tomtruyen.automation.test", intent.dataString)
    }
}
