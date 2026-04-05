package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable

private const val FAST_NOTIFICATION_RESPONSIVENESS_MILLIS = 5_000
private const val BALANCED_NOTIFICATION_RESPONSIVENESS_MILLIS = 30_000
private const val RELAXED_NOTIFICATION_RESPONSIVENESS_MILLIS = 120_000

@Serializable
enum class GeofenceUpdateRate(val notificationResponsivenessMillis: Int) {
    FAST(FAST_NOTIFICATION_RESPONSIVENESS_MILLIS),
    BALANCED(BALANCED_NOTIFICATION_RESPONSIVENESS_MILLIS),
    RELAXED(RELAXED_NOTIFICATION_RESPONSIVENESS_MILLIS),
    ;
}
