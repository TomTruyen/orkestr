package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.DoNotDisturbMode

data class DoNotDisturbModeChangedEvent(val mode: DoNotDisturbMode) : AutomationEvent()
