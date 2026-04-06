package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.AppLifecycleTransitionType

data class ApplicationLifecycleEvent(val packageName: String, val transitionType: AppLifecycleTransitionType) :
    AutomationEvent()
