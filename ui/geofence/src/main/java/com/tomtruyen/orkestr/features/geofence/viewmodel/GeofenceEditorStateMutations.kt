package com.tomtruyen.orkestr.features.geofence.viewmodel

import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.orkestr.features.geofence.state.GeofenceEditorState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceMapPickerState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import com.tomtruyen.orkestr.ui.geofence.R
import java.util.Locale

internal fun GeofenceEditorState.withName(value: String): GeofenceEditorState = copy(
    name = value,
    errors = emptyList(),
)

internal fun GeofenceEditorState.withAddressQuery(value: String): GeofenceEditorState = copy(
    addressQuery = value,
    errors = emptyList(),
)

internal fun GeofenceEditorState.withLatitudeInput(value: String): GeofenceEditorState {
    val parsed = value.toDoubleOrNull()
    return copy(latitudeText = value, mapLatitude = parsed ?: mapLatitude, errors = emptyList())
}

internal fun GeofenceEditorState.withLongitudeInput(value: String): GeofenceEditorState {
    val parsed = value.toDoubleOrNull()
    return copy(longitudeText = value, mapLongitude = parsed ?: mapLongitude, errors = emptyList())
}

internal fun GeofenceEditorState.withRadiusInput(value: String): GeofenceEditorState {
    val parsed = value.toFloatOrNull()
    return copy(radiusText = value, mapRadiusMeters = parsed ?: mapRadiusMeters, errors = emptyList())
}

internal fun GeofenceEditorState.withMapLocation(latitude: Double, longitude: Double): GeofenceEditorState = copy(
    latitudeText = latitude.formatCoordinate(),
    longitudeText = longitude.formatCoordinate(),
    mapLatitude = latitude,
    mapLongitude = longitude,
    errors = emptyList(),
)

internal fun GeofenceEditorState.withSearchResult(result: GeofenceSearchResult): GeofenceEditorState = copy(
    addressQuery = result.title,
    selectedAddress = result.title,
    searchResults = emptyList(),
    latitudeText = result.latitude.formatCoordinate(),
    longitudeText = result.longitude.formatCoordinate(),
    mapLatitude = result.latitude,
    mapLongitude = result.longitude,
    errors = emptyList(),
)

internal fun GeofenceEditorState.withSearchOutcome(
    results: List<GeofenceSearchResult>,
    noResultsError: String,
): GeofenceEditorState = copy(
    searchResults = results,
    errors = if (results.isEmpty()) listOf(noResultsError) else emptyList(),
)

internal fun GeofenceEditorState.withErrors(errors: List<String>): GeofenceEditorState = copy(errors = errors)

internal fun GeofenceEditorState.toMapPickerState(): GeofenceMapPickerState = GeofenceMapPickerState(
    latitude = mapLatitude,
    longitude = mapLongitude,
    addressQuery = addressQuery,
    selectedAddress = selectedAddress,
    searchResults = searchResults,
)

internal fun GeofenceEditorState.applyMapPicker(state: GeofenceMapPickerState): GeofenceEditorState = copy(
    addressQuery = state.addressQuery,
    selectedAddress = state.selectedAddress,
    searchResults = emptyList(),
    latitudeText = state.latitude.formatCoordinate(),
    longitudeText = state.longitude.formatCoordinate(),
    mapLatitude = state.latitude,
    mapLongitude = state.longitude,
    errors = emptyList(),
)

internal fun GeofenceEditorState.validate(stringResolver: (Int) -> String): List<String> {
    val latitude = latitudeText.toDoubleOrNull()
    val longitude = longitudeText.toDoubleOrNull()
    val radius = radiusText.toFloatOrNull()
    val errors = mutableListOf<String>()

    if (name.isBlank()) errors += stringResolver(R.string.geofence_error_name_required)
    if (latitude == null || latitude !in MIN_LATITUDE..MAX_LATITUDE) {
        errors += stringResolver(R.string.geofence_error_latitude_invalid)
    }
    if (longitude == null || longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
        errors += stringResolver(R.string.geofence_error_longitude_invalid)
    }
    if (radius == null || radius <= 0f) {
        errors += stringResolver(R.string.geofence_error_radius_invalid)
    }
    return errors
}

internal fun GeofenceEditorState.toDomainOrNull(): AutomationGeofence? {
    val latitude = latitudeText.toDoubleOrNull() ?: return null
    val longitude = longitudeText.toDoubleOrNull() ?: return null
    val radius = radiusText.toFloatOrNull() ?: return null
    return AutomationGeofence(
        id = id,
        name = name.trim(),
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radius,
        address = selectedAddress ?: addressQuery.takeIf { it.isNotBlank() },
    )
}

internal fun GeofenceMapPickerState.withAddressQuery(value: String): GeofenceMapPickerState = copy(
    addressQuery = value,
    errors = emptyList(),
)

internal fun GeofenceMapPickerState.withLocation(latitude: Double, longitude: Double): GeofenceMapPickerState = copy(
    latitude = latitude,
    longitude = longitude,
    errors = emptyList(),
)

internal fun GeofenceMapPickerState.withSearchResult(result: GeofenceSearchResult): GeofenceMapPickerState = copy(
    addressQuery = result.title,
    selectedAddress = result.title,
    searchResults = emptyList(),
    latitude = result.latitude,
    longitude = result.longitude,
    errors = emptyList(),
)

internal fun GeofenceMapPickerState.withSearchOutcome(
    results: List<GeofenceSearchResult>,
    noResultsError: String,
): GeofenceMapPickerState = copy(
    searchResults = results,
    errors = if (results.isEmpty()) listOf(noResultsError) else emptyList(),
)

internal fun GeofenceMapPickerState.withErrors(errors: List<String>): GeofenceMapPickerState = copy(errors = errors)

internal fun defaultEditorState(id: String, latitude: Double, longitude: Double, radius: Float): GeofenceEditorState =
    GeofenceEditorState(
        id = id,
        latitudeText = latitude.formatCoordinate(),
        longitudeText = longitude.formatCoordinate(),
        radiusText = radius.formatRadius(),
        mapLatitude = latitude,
        mapLongitude = longitude,
        mapRadiusMeters = radius,
    )

internal fun Double.formatCoordinate(): String = String.format(Locale.US, "%.6f", this)

private fun Float.formatRadius(): String = String.format(Locale.US, "%.0f", this)

private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0
