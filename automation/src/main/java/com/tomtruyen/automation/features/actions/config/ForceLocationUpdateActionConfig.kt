package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.BackgroundLocationPermission
import com.tomtruyen.automation.core.permission.FineLocationPermission
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(ForceLocationUpdateActionConfig.DISCRIMINATOR)
data class ForceLocationUpdateActionConfig(val highAccuracy: Boolean = true) : ActionConfig {
    override val type: ActionType = ActionType.FORCE_LOCATION_UPDATE
    override val category: AutomationCategory = AutomationCategory.LOCATION

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(
        FineLocationPermission,
        BackgroundLocationPermission,
    )

    companion object {
        const val DISCRIMINATOR = "force_location_update"
    }
}
