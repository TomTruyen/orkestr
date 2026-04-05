package com.tomtruyen.automation.features.constraints.delegate

import com.tomtruyen.automation.codegen.GenerateConstraintDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig

@GenerateConstraintDelegate
class BatteryLevelConstraintDelegate : ConstraintDelegate<BatteryLevelConstraintConfig> {
    override val type: ConstraintType = ConstraintType.BATTERY_LEVEL

    override suspend fun evaluate(config: BatteryLevelConstraintConfig, event: AutomationEvent): Boolean {
        if (event !is BatteryChangedEvent || event.scale <= 0) return false

        val batteryPercent = (event.level * BATTERY_PERCENT_SCALE) / event.scale.toFloat()
        return config.operator.matches(batteryPercent, config.value.toFloat())
    }

    private companion object {
        private const val BATTERY_PERCENT_SCALE = 100f
    }
}
