package com.tomtruyen.orkestr.features.geofence.state

import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig

sealed interface GeofenceTriggerAction {
    data class LoadConfig(val config: GeofenceTriggerConfig) : GeofenceTriggerAction
    data class LoadConstraintConfig(val config: GeofenceConstraintConfig) : GeofenceTriggerAction
    data class SelectGeofence(val geofenceId: String) : GeofenceTriggerAction
    data class SelectTransition(val transitionType: GeofenceTransitionType) : GeofenceTriggerAction
    data object SaveConfigurationClicked : GeofenceTriggerAction
    data object CreateGeofenceClicked : GeofenceTriggerAction
    data object CloseGeofenceEditorClicked : GeofenceTriggerAction
    data object OpenMapPickerClicked : GeofenceTriggerAction
    data object CloseMapPickerClicked : GeofenceTriggerAction
    data class GeofenceNameChanged(val value: String) : GeofenceTriggerAction
    data class GeofenceLatitudeChanged(val value: String) : GeofenceTriggerAction
    data class GeofenceLongitudeChanged(val value: String) : GeofenceTriggerAction
    data class GeofenceRadiusChanged(val value: String) : GeofenceTriggerAction
    data class GeofenceAddressQueryChanged(val value: String) : GeofenceTriggerAction
    data object GeofenceAddressSearchClicked : GeofenceTriggerAction
    data class GeofenceSearchResultSelected(val result: GeofenceSearchResult) : GeofenceTriggerAction
    data class GeofenceMapLocationSelected(val latitude: Double, val longitude: Double) : GeofenceTriggerAction
    data class MapPickerAddressQueryChanged(val value: String) : GeofenceTriggerAction
    data object MapPickerAddressSearchClicked : GeofenceTriggerAction
    data class MapPickerSearchResultSelected(val result: GeofenceSearchResult) : GeofenceTriggerAction
    data class MapPickerLocationSelected(val latitude: Double, val longitude: Double) : GeofenceTriggerAction
    data object ConfirmMapPickerClicked : GeofenceTriggerAction
    data object SaveGeofenceClicked : GeofenceTriggerAction
}
