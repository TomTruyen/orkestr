package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.FineLocationPermission
import com.tomtruyen.automation.core.permission.NearbyWifiDevicesPermission
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(WifiSsidConstraintConfig.DISCRIMINATOR)
data class WifiSsidConstraintConfig(val ssid: String = "") : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.WIFI_SSID_CONNECTED
    override val category: AutomationCategory = AutomationCategory.UTILITY

    @Transient
    override val requiredPermissions: List<AutomationPermission> =
        listOf(FineLocationPermission, NearbyWifiDevicesPermission)

    companion object {
        const val DISCRIMINATOR = "wifi_ssid_connected_constraint"
    }
}
