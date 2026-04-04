package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.Serializable

@Serializable
sealed interface ActionConfig {
    val type: ActionType
}
