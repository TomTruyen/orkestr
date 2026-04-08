package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatterySaverStateConstraintConfig

@GenerateConstraintDelegate
class BatterySaverStateConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<BatterySaverStateConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.BATTERY_SAVER_STATE

    override suspend fun evaluate(config: BatterySaverStateConstraintConfig): Boolean =
        deviceStateReader.isBatterySaverEnabled() == config.enabled
}
