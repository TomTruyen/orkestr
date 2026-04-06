package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.PowerConnectionState

data class PowerConnectionEvent(val state: PowerConnectionState) : AutomationEvent()
