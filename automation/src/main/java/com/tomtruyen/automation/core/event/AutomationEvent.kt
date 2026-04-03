package com.tomtruyen.automation.core.event

interface AutomationEvent {
    val timestampMillis: Long
}

// TODO: Add specific events like: Manual, ChargingStateChanged,...