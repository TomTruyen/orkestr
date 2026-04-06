package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Serializable
enum class Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    ;

    companion object {
        fun from(dayOfWeek: DayOfWeek): Weekday = when (dayOfWeek) {
            DayOfWeek.MONDAY -> MONDAY
            DayOfWeek.TUESDAY -> TUESDAY
            DayOfWeek.WEDNESDAY -> WEDNESDAY
            DayOfWeek.THURSDAY -> THURSDAY
            DayOfWeek.FRIDAY -> FRIDAY
            DayOfWeek.SATURDAY -> SATURDAY
            DayOfWeek.SUNDAY -> SUNDAY
        }
    }
}
