package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.WifiStateConstraintConfig

@GenerateConstraintDelegate
class WifiStateConstraintDelegate(context: Context) : LiveStateConstraintDelegate<WifiStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.WIFI_STATE

    override suspend fun evaluate(config: WifiStateConstraintConfig): Boolean =
        deviceStateReader.isWifiEnabled() == config.enabled
}
