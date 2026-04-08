package com.tomtruyen.automation.core.state

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.permission.BluetoothConnectPermission
import com.tomtruyen.automation.core.permission.FineLocationPermission
import com.tomtruyen.automation.core.permission.NearbyWifiDevicesPermission
import com.tomtruyen.automation.core.permission.ReadPhoneStatePermission
import java.time.LocalDateTime

class DeviceStateReader(private val context: Context) {
    fun batteryPercent(): Int? {
        val batteryManager = context.getSystemService(BatteryManager::class.java)
        val percent = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: INVALID_LEVEL
        return percent.takeIf { it in MIN_BATTERY_PERCENT..MAX_BATTERY_PERCENT }
    }

    fun isBatterySaverEnabled(): Boolean = context.getSystemService(PowerManager::class.java)?.isPowerSaveMode == true

    fun isPowerConnected(): Boolean {
        val batteryStatus = batteryStatusIntent() ?: return false
        val plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        return plugged != 0
    }

    fun isBluetoothEnabled(): Boolean {
        if (!hasBluetoothReadAccess()) return false
        return context.getSystemService(BluetoothManager::class.java)?.adapter?.isEnabled == true
    }

    fun isGpsEnabled(): Boolean =
        context.getSystemService(LocationManager::class.java)?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true

    @SuppressLint("MissingPermission")
    fun isMobileDataEnabled(): Boolean {
        if (!hasPhoneStateAccess()) return false
        val telephonyManager = context.getSystemService(TelephonyManager::class.java) ?: return false
        return telephonyManager.isDataEnabled
    }

    fun isWifiEnabled(): Boolean =
        context.applicationContext.getSystemService(WifiManager::class.java)?.isWifiEnabled == true

    @SuppressLint("MissingPermission")
    fun connectedWifiSsid(): String? {
        if (!hasWifiAccess()) return null
        val wifiManager = context.applicationContext.getSystemService(WifiManager::class.java) ?: return null
        return wifiManager.connectionInfo?.ssid
            ?.trim()
            ?.takeUnless { it.equals(WifiManager.UNKNOWN_SSID, ignoreCase = true) }
            ?.removeSurrounding("\"")
    }

    fun currentDateTime(): LocalDateTime = LocalDateTime.now()

    @SuppressLint("MissingPermission")
    fun isInsideGeofence(latitude: Double, longitude: Double, radiusMeters: Float): Boolean {
        if (!hasFineLocationAccess()) return false
        val locationManager = context.getSystemService(LocationManager::class.java) ?: return false
        val bestLocation = locationManager.getProviders(true)
            .mapNotNull(locationManager::getLastKnownLocation)
            .maxByOrNull(Location::getTime)
            ?: return false
        val result = FloatArray(1)
        Location.distanceBetween(
            bestLocation.latitude,
            bestLocation.longitude,
            latitude,
            longitude,
            result,
        )
        return result.firstOrNull()?.let { distance -> distance <= radiusMeters } == true
    }

    fun isHeadphonesConnected(): Boolean {
        val audioManager = context.getSystemService(AudioManager::class.java) ?: return false
        return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { device ->
            device.type in HEADPHONE_DEVICE_TYPES
        }
    }

    fun isMusicActive(): Boolean = context.getSystemService(AudioManager::class.java)?.isMusicActive == true

    @SuppressLint("MissingPermission")
    fun isCallActive(): Boolean {
        if (!hasPhoneStateAccess()) return false
        val telephonyManager = context.getSystemService(TelephonyManager::class.java) ?: return false
        return telephonyManager.callState != TelephonyManager.CALL_STATE_IDLE
    }

    fun isScreenInteractive(): Boolean = context.getSystemService(PowerManager::class.java)?.isInteractive == true

    private fun batteryStatusIntent(): Intent? = ContextCompat.registerReceiver(
        context,
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ContextCompat.RECEIVER_NOT_EXPORTED,
    )

    private fun hasFineLocationAccess(): Boolean = FineLocationPermission.isGranted(context)

    private fun hasWifiAccess(): Boolean =
        FineLocationPermission.isGranted(context) && NearbyWifiDevicesPermission.isGranted(context)

    private fun hasPhoneStateAccess(): Boolean = ReadPhoneStatePermission.isGranted(context)

    private fun hasBluetoothReadAccess(): Boolean = BluetoothConnectPermission.isGranted(context)

    private companion object {
        const val INVALID_LEVEL = Int.MIN_VALUE
        const val MIN_BATTERY_PERCENT = 0
        const val MAX_BATTERY_PERCENT = 100

        val HEADPHONE_DEVICE_TYPES = setOf(
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
            AudioDeviceInfo.TYPE_USB_HEADSET,
            AudioDeviceInfo.TYPE_BLE_HEADSET,
        )
    }
}
