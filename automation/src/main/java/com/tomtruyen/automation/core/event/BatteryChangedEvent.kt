package com.tomtruyen.automation.core.event

data class BatteryChangedEvent(
    val level: Int,
    val scale: Int,
    val status: Int,
    val plugged: Int,
): AutomationEvent() {
}