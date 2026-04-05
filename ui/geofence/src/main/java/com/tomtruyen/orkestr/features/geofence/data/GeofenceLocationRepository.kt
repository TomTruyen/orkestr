package com.tomtruyen.orkestr.features.geofence.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class GeofenceLocation(val latitude: Double, val longitude: Double)

class GeofenceLocationRepository(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationOrNull(): GeofenceLocation? = runCatching {
        fusedLocationClient.lastLocation.await()?.toGeofenceLocation()
    }.getOrNull()
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { error -> continuation.resumeWithException(error) }
    addOnCanceledListener { continuation.cancel() }
}

private fun Location.toGeofenceLocation(): GeofenceLocation = GeofenceLocation(
    latitude = latitude,
    longitude = longitude,
)
