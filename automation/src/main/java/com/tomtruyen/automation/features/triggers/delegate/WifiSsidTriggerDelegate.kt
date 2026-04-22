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
        val currentInRange = inRange(
            expected = expected,
            connectedSsid = event.connectedSsid,
            visibleSsids = event.visibleSsids,
        )
        val previousInRange = event.previousVisibleSsids?.let {
            inRange(
                expected = expected,
                connectedSsid = event.previousConnectedSsid,
                visibleSsids = it,
            )
        } ?: return false
        return when (config.triggerType) {
            WifiRangeTriggerType.IN_RANGE -> !previousInRange && currentInRange
            WifiRangeTriggerType.OUT_OF_RANGE -> previousInRange && !currentInRange
        }
    }

    private fun inRange(expected: String, connectedSsid: String?, visibleSsids: Set<String>): Boolean =
        expected == normalizeSsid(connectedSsid) ||
            expected in visibleSsids.map(::normalizeSsid).toSet()

    private fun normalizeSsid(ssid: String?): String = ssid.orEmpty().trim().removeSurrounding("\"")
}
