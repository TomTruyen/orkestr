package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.codegen.GenerateTriggerDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.WifiScanResultEvent
import com.tomtruyen.automation.core.model.WifiRangeTriggerType
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig

@GenerateTriggerDelegate
class WifiSsidTriggerDelegate : TriggerDelegate<WifiSsidTriggerConfig> {
    override val type: TriggerType = TriggerType.WIFI_SSID_IN_RANGE

    override fun matches(config: WifiSsidTriggerConfig, event: AutomationEvent): Boolean {
        if (event !is WifiScanResultEvent) return false
        val expected = normalizeSsid(config.ssid)
        val inRange = expected == normalizeSsid(event.connectedSsid) ||
            expected in event.visibleSsids.map(::normalizeSsid).toSet()
        return when (config.triggerType) {
            WifiRangeTriggerType.IN_RANGE -> inRange
            WifiRangeTriggerType.OUT_OF_RANGE -> !inRange
        }
    }

    private fun normalizeSsid(ssid: String?): String = ssid.orEmpty().trim().removeSurrounding("\"")
}
