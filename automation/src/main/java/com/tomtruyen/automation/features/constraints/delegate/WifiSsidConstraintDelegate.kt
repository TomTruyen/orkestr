package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig

@GenerateConstraintDelegate
class WifiSsidConstraintDelegate(context: Context) : LiveStateConstraintDelegate<WifiSsidConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.WIFI_SSID_CONNECTED

    override suspend fun evaluate(config: WifiSsidConstraintConfig): Boolean =
        normalizeSsid(deviceStateReader.connectedWifiSsid()) == normalizeSsid(config.ssid)

    private fun normalizeSsid(ssid: String?): String = ssid.orEmpty().trim().removeSurrounding("\"")
}
