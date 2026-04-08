package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class WifiSsidConstraintDelegateTest {
    @Test
    fun evaluate_whenConnectedToConfiguredSsid_returnsTrue() = runTest {
        val delegate = delegateWithConnectedSsid("\"Office WiFi\"")

        val result = delegate.evaluate(WifiSsidConstraintConfig(ssid = "Office WiFi"), mockk<AutomationEvent>())

        assertTrue(result)
    }

    @Test
    fun evaluate_whenConnectedToDifferentSsid_returnsFalse() = runTest {
        val delegate = delegateWithConnectedSsid("\"Guest WiFi\"")

        val result = delegate.evaluate(WifiSsidConstraintConfig(ssid = "Office WiFi"), mockk<AutomationEvent>())

        assertFalse(result)
    }

    @Test
    fun evaluate_whenSsidUnavailable_returnsFalse() = runTest {
        val delegate = delegateWithConnectedSsid(ssid = WifiManager.UNKNOWN_SSID)

        val result = delegate.evaluate(WifiSsidConstraintConfig(ssid = "Office WiFi"), mockk<AutomationEvent>())

        assertFalse(result)
    }

    private fun delegateWithConnectedSsid(ssid: String): WifiSsidConstraintDelegate {
        val wifiInfo = mockk<WifiInfo>()
        every { wifiInfo.ssid } returns ssid

        val wifiManager = mockk<WifiManager>()
        every { wifiManager.connectionInfo } returns wifiInfo

        val context = mockk<Context>()
        every { context.applicationContext } returns context
        every { context.getSystemService(WifiManager::class.java) } returns wifiManager
        every { context.checkSelfPermission(any()) } returns android.content.pm.PackageManager.PERMISSION_GRANTED

        return WifiSsidConstraintDelegate(context)
    }
}
