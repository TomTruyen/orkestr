package com.tomtruyen.automation.core.model

import android.os.BatteryManager
import kotlinx.serialization.Serializable

@Serializable
enum class BatteryChargeState {
    UNKNOWN,
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING;

    companion object {
        fun fromBatteryManagerState(status: Int): BatteryChargeState = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> NOT_CHARGING
            else -> UNKNOWN
        }
    }
}