package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.GeofenceTransitionType
import com.tomtruyen.automation.core.model.GeofenceUpdateRate
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.BackgroundLocationPermission
import com.tomtruyen.automation.core.permission.FineLocationPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(GeofenceTriggerConfig.DISCRIMINATOR)
data class GeofenceTriggerConfig(
    val geofenceId: String = "",
    val geofenceName: String = "",
    val transitionType: GeofenceTransitionType = GeofenceTransitionType.ENTER,
    val updateRate: GeofenceUpdateRate = GeofenceUpdateRate.BALANCED,
) : TriggerConfig {
    override val type: TriggerType = TriggerType.GEOFENCE
    override val category: AutomationCategory = AutomationCategory.LOCATION
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.GEOFENCE)

    @Transient
    override val requiredPermissions: List<AutomationPermission> =
        listOf(FineLocationPermission, BackgroundLocationPermission)

    companion object {
        const val DISCRIMINATOR = "geofence"
    }
}
