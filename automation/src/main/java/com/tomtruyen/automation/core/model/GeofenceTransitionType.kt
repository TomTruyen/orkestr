package com.tomtruyen.automation.core.model

import com.google.android.gms.location.Geofence
import kotlinx.serialization.Serializable

@Serializable
enum class GeofenceTransitionType {
    ENTER,
    EXIT,
    ;

    fun toGoogleTransition(): Int = when (this) {
        ENTER -> Geofence.GEOFENCE_TRANSITION_ENTER
        EXIT -> Geofence.GEOFENCE_TRANSITION_EXIT
    }

    companion object {
        fun fromGoogleTransition(value: Int): GeofenceTransitionType? = when (value) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> ENTER
            Geofence.GEOFENCE_TRANSITION_EXIT -> EXIT
            else -> null
        }
    }
}
