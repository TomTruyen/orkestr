package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.Serializable

@Serializable
sealed interface ActionConfig : AutomationConfig<ActionType> {
    val parallelExecutionConflictGroup: ActionExecutionConflictGroup?
        get() = null

    val parallelExecutionConflictPriority: Int
        get() = 0
}
