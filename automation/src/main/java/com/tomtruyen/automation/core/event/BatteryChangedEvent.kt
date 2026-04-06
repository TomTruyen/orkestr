package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus

data class BatteryChangedEvent(
    val level: Int,
    val scale: Int,
    val chargeState: BatteryChargeState,
    val plugStatus: BatteryPlugStatus,
    val previousLevel: Int? = null,
    val previousScale: Int? = null,
    val previousChargeState: BatteryChargeState? = null,
    val previousPlugStatus: BatteryPlugStatus? = null,
) : AutomationEvent() {
    val percentage: Int
        get() = if (scale <= 0) 0 else ((level * 100f) / scale).toInt()

    val previousPercentage: Int?
        get() = previousLevel?.let { level ->
            val scale = previousScale ?: return null
            if (scale <= 0) 0 else ((level * 100f) / scale).toInt()
        }
}
