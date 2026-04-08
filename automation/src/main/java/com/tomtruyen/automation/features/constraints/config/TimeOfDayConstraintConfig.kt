package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.AutomationLocalTime
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
@SerialName(TimeOfDayConstraintConfig.DISCRIMINATOR)
data class TimeOfDayConstraintConfig(
    val startTime: AutomationLocalTime = AutomationLocalTime(hour = 9, minute = 0),
    val endTime: AutomationLocalTime = AutomationLocalTime(hour = 17, minute = 0),
) : ConstraintConfig {
    override val type: ConstraintType = ConstraintType.TIME_OF_DAY
    override val category: AutomationCategory = AutomationCategory.UTILITY

    fun matches(time: LocalTime): Boolean {
        val start = startTime.toLocalTime()
        val end = endTime.toLocalTime()
        return if (start <= end) {
            time in start..end
        } else {
            time >= start || time <= end
        }
    }

    companion object {
        const val DISCRIMINATOR = "time_of_day_constraint"
    }
}
