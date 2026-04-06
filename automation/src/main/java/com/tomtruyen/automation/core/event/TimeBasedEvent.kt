package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.Weekday

data class TimeBasedEvent(val hour: Int, val minute: Int, val day: Weekday) : AutomationEvent()
