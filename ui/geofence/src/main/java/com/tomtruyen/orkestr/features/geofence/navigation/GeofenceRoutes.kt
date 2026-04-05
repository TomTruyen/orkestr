package com.tomtruyen.orkestr.features.geofence.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object GeofenceTriggerConfigurationRoute : NavKey

@Serializable
data object GeofenceEditorRoute : NavKey

@Serializable
data object GeofenceMapPickerRoute : NavKey
