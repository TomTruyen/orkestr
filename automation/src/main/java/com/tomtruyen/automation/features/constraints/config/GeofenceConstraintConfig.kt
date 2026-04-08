package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.BackgroundLocationPermission
import com.tomtruyen.automation.core.permission.FineLocationPermission
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(GeofenceConstraintConfig.DISCRIMINATOR)
data class GeofenceConstraintConfig(
    val geofenceId: String = "",
    val geofenceName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Float = 150f,
    val inside: Boolean = true,
) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.GEOFENCE
    override val category: AutomationCategory = AutomationCategory.LOCATION

    @Transient
    override val requiredPermissions: List<AutomationPermission> =
        listOf(FineLocationPermission, BackgroundLocationPermission)

    companion object {
        const val DISCRIMINATOR = "geofence_constraint"
    }
}
