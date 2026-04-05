package com.tomtruyen.orkestr.features.geofence.state

data class GeofenceEditorState(
    val id: String,
    val name: String = "",
    val latitudeText: String = "0.0",
    val longitudeText: String = "0.0",
    val radiusText: String = "150",
    val mapLatitude: Double = 0.0,
    val mapLongitude: Double = 0.0,
    val mapRadiusMeters: Float = 150f,
    val addressQuery: String = "",
    val selectedAddress: String? = null,
    val searchResults: List<GeofenceSearchResult> = emptyList(),
    val errors: List<String> = emptyList(),
)
