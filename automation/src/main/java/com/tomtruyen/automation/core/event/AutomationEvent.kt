package com.tomtruyen.automation.core.event

abstract class AutomationEvent {
    val timestampMillis: Long = System.currentTimeMillis()
}