package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(DayOfWeekConstraintConfig.DISCRIMINATOR)
data class DayOfWeekConstraintConfig(val days: Set<Weekday> = setOf(Weekday.MONDAY)) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.DAY_OF_WEEK
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "day_of_week_constraint"
    }
}
