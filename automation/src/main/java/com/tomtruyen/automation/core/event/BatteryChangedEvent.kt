package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus

data class BatteryChangedEvent(
    val level: Int,
    val scale: Int,
    val chargeState: BatteryChargeState,
    val plugStatus: BatteryPlugStatus,
): AutomationEvent()
