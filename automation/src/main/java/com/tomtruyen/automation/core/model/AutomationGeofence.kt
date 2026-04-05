package com.tomtruyen.automation.core.model

data class AutomationGeofence(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val address: String? = null,
)
