package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@GenerateConstraintDelegate
class GeofenceConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<GeofenceConstraintConfig>(context),
    KoinComponent {
    override val type: ConstraintType = ConstraintType.GEOFENCE
    private val geofenceRepository: GeofenceRepository by inject()

    override suspend fun evaluate(config: GeofenceConstraintConfig): Boolean {
        val resolvedGeofence = config.geofenceId.takeIf(String::isNotBlank)?.let { geofenceId ->
            geofenceRepository.getGeofence(geofenceId)
        }
        val insideGeofence = deviceStateReader.isInsideGeofence(
            latitude = resolvedGeofence?.latitude ?: config.latitude,
            longitude = resolvedGeofence?.longitude ?: config.longitude,
            radiusMeters = resolvedGeofence?.radiusMeters ?: config.radiusMeters,
        )
        return insideGeofence == config.inside
    }
}
