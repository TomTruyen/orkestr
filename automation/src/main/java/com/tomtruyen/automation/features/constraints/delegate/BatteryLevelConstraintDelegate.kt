package com.tomtruyen.automation.features.constraints.delegate

import android.content.Context
import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig

@GenerateConstraintDelegate
class BatteryLevelConstraintDelegate(context: Context) :
    LiveStateConstraintDelegate<BatteryLevelConstraintConfig>(context) {
    override val type: ConstraintType = ConstraintType.BATTERY_LEVEL

    override suspend fun evaluate(config: BatteryLevelConstraintConfig): Boolean {
        val batteryPercent = deviceStateReader.batteryPercent() ?: return false
        return config.operator.matches(batteryPercent.toFloat(), config.value.toFloat())
    }
}
