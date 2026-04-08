package com.tomtruyen.automation.features.constraints.definition

import androidx.annotation.StringRes
import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.model.MonthOfYear
import com.tomtruyen.automation.core.model.Weekday

@StringRes
internal fun Boolean.enabledDisabledRes(): Int = if (this) {
    R.string.automation_definition_value_enabled
} else {
    R.string.automation_definition_value_disabled
}

@StringRes
internal fun Boolean.connectedDisconnectedRes(): Int = if (this) {
    R.string.automation_definition_value_connected
} else {
    R.string.automation_definition_value_disconnected
}

@StringRes
internal fun Boolean.onOffRes(): Int = if (this) {
    R.string.automation_definition_value_on
} else {
    R.string.automation_definition_value_off
}

@StringRes
internal fun Boolean.activeInactiveRes(): Int = if (this) {
    R.string.automation_definition_value_active
} else {
    R.string.automation_definition_value_inactive
}

@StringRes
internal fun Boolean.inCallIdleRes(): Int = if (this) {
    R.string.automation_definition_value_in_call
} else {
    R.string.automation_definition_value_idle
}

@StringRes
internal fun Boolean.insideOutsideRes(): Int = if (this) {
    R.string.automation_definition_value_inside
} else {
    R.string.automation_definition_value_outside
}

@StringRes
internal fun Weekday.weekdayLabelRes(): Int = when (this) {
    Weekday.MONDAY -> R.string.automation_definition_weekday_monday
    Weekday.TUESDAY -> R.string.automation_definition_weekday_tuesday
    Weekday.WEDNESDAY -> R.string.automation_definition_weekday_wednesday
    Weekday.THURSDAY -> R.string.automation_definition_weekday_thursday
    Weekday.FRIDAY -> R.string.automation_definition_weekday_friday
    Weekday.SATURDAY -> R.string.automation_definition_weekday_saturday
    Weekday.SUNDAY -> R.string.automation_definition_weekday_sunday
}

@StringRes
internal fun MonthOfYear.monthLabelRes(): Int = when (this) {
    MonthOfYear.JANUARY -> R.string.automation_definition_month_january
    MonthOfYear.FEBRUARY -> R.string.automation_definition_month_february
    MonthOfYear.MARCH -> R.string.automation_definition_month_march
    MonthOfYear.APRIL -> R.string.automation_definition_month_april
    MonthOfYear.MAY -> R.string.automation_definition_month_may
    MonthOfYear.JUNE -> R.string.automation_definition_month_june
    MonthOfYear.JULY -> R.string.automation_definition_month_july
    MonthOfYear.AUGUST -> R.string.automation_definition_month_august
    MonthOfYear.SEPTEMBER -> R.string.automation_definition_month_september
    MonthOfYear.OCTOBER -> R.string.automation_definition_month_october
    MonthOfYear.NOVEMBER -> R.string.automation_definition_month_november
    MonthOfYear.DECEMBER -> R.string.automation_definition_month_december
}

@StringRes
internal fun dayOfMonthLabelRes(day: Int): Int = DAY_OF_MONTH_LABELS.getOrElse(day - 1) {
    R.string.automation_definition_day_of_month_31
}

private val DAY_OF_MONTH_LABELS = listOf(
    R.string.automation_definition_day_of_month_1,
    R.string.automation_definition_day_of_month_2,
    R.string.automation_definition_day_of_month_3,
    R.string.automation_definition_day_of_month_4,
    R.string.automation_definition_day_of_month_5,
    R.string.automation_definition_day_of_month_6,
    R.string.automation_definition_day_of_month_7,
    R.string.automation_definition_day_of_month_8,
    R.string.automation_definition_day_of_month_9,
    R.string.automation_definition_day_of_month_10,
    R.string.automation_definition_day_of_month_11,
    R.string.automation_definition_day_of_month_12,
    R.string.automation_definition_day_of_month_13,
    R.string.automation_definition_day_of_month_14,
    R.string.automation_definition_day_of_month_15,
    R.string.automation_definition_day_of_month_16,
    R.string.automation_definition_day_of_month_17,
    R.string.automation_definition_day_of_month_18,
    R.string.automation_definition_day_of_month_19,
    R.string.automation_definition_day_of_month_20,
    R.string.automation_definition_day_of_month_21,
    R.string.automation_definition_day_of_month_22,
    R.string.automation_definition_day_of_month_23,
    R.string.automation_definition_day_of_month_24,
    R.string.automation_definition_day_of_month_25,
    R.string.automation_definition_day_of_month_26,
    R.string.automation_definition_day_of_month_27,
    R.string.automation_definition_day_of_month_28,
    R.string.automation_definition_day_of_month_29,
    R.string.automation_definition_day_of_month_30,
    R.string.automation_definition_day_of_month_31,
)
