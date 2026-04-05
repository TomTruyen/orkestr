package com.tomtruyen.orkestr.features.geofence.state

import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig

data class GeofenceTriggerUiState(
    val config: GeofenceTriggerConfig = GeofenceTriggerConfig(),
    val geofences: List<AutomationGeofence> = emptyList(),
    val configErrors: List<String> = emptyList(),
    val geofenceEditorState: GeofenceEditorState? = null,
    val mapPickerState: GeofenceMapPickerState? = null,
)
