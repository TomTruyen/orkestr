package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable
import java.time.Month

@Serializable
enum class MonthOfYear {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER,
    ;

    companion object {
        fun from(month: Month): MonthOfYear = when (month) {
            Month.JANUARY -> JANUARY
            Month.FEBRUARY -> FEBRUARY
            Month.MARCH -> MARCH
            Month.APRIL -> APRIL
            Month.MAY -> MAY
            Month.JUNE -> JUNE
            Month.JULY -> JULY
            Month.AUGUST -> AUGUST
            Month.SEPTEMBER -> SEPTEMBER
            Month.OCTOBER -> OCTOBER
            Month.NOVEMBER -> NOVEMBER
            Month.DECEMBER -> DECEMBER
        }
    }
}
