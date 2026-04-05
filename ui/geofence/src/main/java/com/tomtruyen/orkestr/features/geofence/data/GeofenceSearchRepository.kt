package com.tomtruyen.orkestr.features.geofence.data

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GeofenceSearchRepository(private val context: Context) {
    fun isAvailable(): Boolean = Geocoder.isPresent()

    suspend fun search(query: String): List<GeofenceSearchResult> {
        if (query.isBlank() || !isAvailable()) return emptyList()
        val geocoder = Geocoder(context, Locale.getDefault())
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.awaitResults(query)
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(query, 5).orEmpty()
            }
        }.mapNotNull(Address::toSearchResult)
    }
}

private suspend fun Geocoder.awaitResults(query: String): List<Address> = suspendCancellableCoroutine { continuation ->
    getFromLocationName(
        query,
        5,
        object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                continuation.resume(addresses.toList())
            }

            override fun onError(errorMessage: String?) {
                continuation.resumeWithException(IllegalStateException(errorMessage ?: "Geocoder failed"))
            }
        },
    )
}

private fun Address.toSearchResult(): GeofenceSearchResult? {
    val latitude = latitude
    val longitude = longitude
    val lines = (0..maxAddressLineIndex).mapNotNull(::getAddressLine).distinct()
    val title = lines.joinToString(", ").ifBlank {
        featureName ?: locality ?: adminArea ?: countryName.orEmpty()
    }
    if (title.isBlank()) return null
    return GeofenceSearchResult(
        title = title,
        latitude = latitude,
        longitude = longitude,
    )
}
