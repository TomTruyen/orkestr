package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class GeofenceUpdateRate(val notificationResponsivenessMillis: Int) {
    FAST(5_000),
    BALANCED(30_000),
    RELAXED(120_000),
}
