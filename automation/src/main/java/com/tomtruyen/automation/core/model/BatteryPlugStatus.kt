package com.tomtruyen.automation.core.model

import android.os.BatteryManager
import kotlinx.serialization.Serializable

@Serializable
enum class BatteryPlugStatus {
    UNPLUGGED,
    AC,
    USB,
    WIRELESS,
    DOCK,
    UNKNOWN;

    companion object {
        fun fromBatteryManagerPluggedStatus(plugged: Int): BatteryPlugStatus = when (plugged) {
            0 -> UNPLUGGED
            BatteryManager.BATTERY_PLUGGED_AC -> AC
            BatteryManager.BATTERY_PLUGGED_USB -> USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> WIRELESS
            BatteryManager.BATTERY_PLUGGED_DOCK -> DOCK
            else -> UNKNOWN
        }
    }
}
