package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AppLifecycleTransitionType {
    LAUNCHED,
    CLOSED,
}
