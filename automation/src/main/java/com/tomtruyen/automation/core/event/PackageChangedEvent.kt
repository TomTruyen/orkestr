package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.PackageChangeType

data class PackageChangedEvent(val packageName: String, val changeType: PackageChangeType) : AutomationEvent()
