package com.tomtruyen.automation.core.event

import com.tomtruyen.automation.core.model.GeofenceTransitionType

data class GeofenceTransitionEvent(val geofenceId: String, val transitionType: GeofenceTransitionType) :
    AutomationEvent()
