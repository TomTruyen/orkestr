package com.tomtruyen.automation.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class WifiRangeTriggerType {
    IN_RANGE,
    OUT_OF_RANGE,
}
