package com.tomtruyen.automation.core.event

data class NotificationReceivedEvent(val packageName: String, val title: String?, val message: String?) :
    AutomationEvent()
