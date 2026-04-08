package com.tomtruyen.orkestr.features.geofence.state

import com.tomtruyen.automation.core.model.AutomationGeofence

sealed interface GeofenceTriggerEvent {
    data object NavigateToGeofenceEditor : GeofenceTriggerEvent
    data object NavigateToMapPicker : GeofenceTriggerEvent
    data object PopBack : GeofenceTriggerEvent
    data class GeofenceSelected(val geofence: AutomationGeofence) : GeofenceTriggerEvent
}
