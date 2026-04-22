package com.tomtruyen.automation.core.event

data class WifiScanResultEvent(
    val visibleSsids: Set<String>,
    val connectedSsid: String?,
    val previousVisibleSsids: Set<String>? = null,
    val previousConnectedSsid: String? = null,
) : AutomationEvent()
