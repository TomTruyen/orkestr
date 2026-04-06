package com.tomtruyen.automation.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class AutomationLocationRequester(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var lastKnownLocation: Location? = null

    @SuppressLint("MissingPermission")
    suspend fun requestCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    lastKnownLocation = location
                }
                continuation.resume(location ?: lastKnownLocation)
            }
            .addOnFailureListener {
                continuation.resume(lastKnownLocation)
            }
        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }
}
