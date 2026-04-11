package com.tomtruyen.automation.core.model

import android.app.NotificationManager
import kotlinx.serialization.Serializable

@Serializable
enum class DoNotDisturbMode {
    PRIORITY_ONLY,
    ALARMS_ONLY,
    TOTAL_SILENCE,
    OFF,
}

fun Int.toDoNotDisturbMode(): DoNotDisturbMode? = when (this) {
    NotificationManager.INTERRUPTION_FILTER_ALL -> DoNotDisturbMode.OFF
    NotificationManager.INTERRUPTION_FILTER_PRIORITY -> DoNotDisturbMode.PRIORITY_ONLY
    NotificationManager.INTERRUPTION_FILTER_ALARMS -> DoNotDisturbMode.ALARMS_ONLY
    NotificationManager.INTERRUPTION_FILTER_NONE -> DoNotDisturbMode.TOTAL_SILENCE
    else -> null
}
