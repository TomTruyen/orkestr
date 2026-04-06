package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.WifiRangeTriggerType
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.FineLocationPermission
import com.tomtruyen.automation.core.permission.NearbyWifiDevicesPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(WifiSsidTriggerConfig.DISCRIMINATOR)
data class WifiSsidTriggerConfig(
    val ssid: String = "",
    val triggerType: WifiRangeTriggerType = WifiRangeTriggerType.IN_RANGE,
) : TriggerConfig {
    override val type: TriggerType = TriggerType.WIFI_SSID_IN_RANGE
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.WIFI_SCAN)

    @Transient
    override val requiredPermissions: List<AutomationPermission> =
        listOf(FineLocationPermission, NearbyWifiDevicesPermission)

    companion object {
        const val DISCRIMINATOR = "wifi_ssid_in_range"
    }
}
