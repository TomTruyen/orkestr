package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.PowerConnectedConstraintConfig

@GenerateConstraintDelegate
class PowerConnectedConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<PowerConnectedConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.POWER_CONNECTED

    override suspend fun evaluate(config: PowerConnectedConstraintConfig): Boolean =
        deviceStateReader.isPowerConnected() == config.connected
}
