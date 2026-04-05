package com.tomtruyen.orkestr.features.geofence.state

data class GeofenceMapPickerState(
    val latitude: Double,
    val longitude: Double,
    val addressQuery: String = "",
    val selectedAddress: String? = null,
    val searchResults: List<GeofenceSearchResult> = emptyList(),
    val errors: List<String> = emptyList(),
)
