package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class AutomationLocalTime(val hour: Int = 0, val minute: Int = 0) {
    init {
        require(hour in MIN_HOUR..MAX_HOUR) { "hour must be between $MIN_HOUR and $MAX_HOUR" }
        require(minute in MIN_MINUTE..MAX_MINUTE) { "minute must be between $MIN_MINUTE and $MAX_MINUTE" }
    }

    fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    fun format24Hour(): String = "%02d:%02d".format(hour, minute)

    companion object {
        private const val MIN_HOUR = 0
        private const val MAX_HOUR = 23
        private const val MIN_MINUTE = 0
        private const val MAX_MINUTE = 59

        fun from(localTime: LocalTime): AutomationLocalTime = AutomationLocalTime(
            hour = localTime.hour,
            minute = localTime.minute,
        )

        fun parse(value: String): AutomationLocalTime? = runCatching {
            from(LocalTime.parse(value))
        }.getOrNull()
    }
}
