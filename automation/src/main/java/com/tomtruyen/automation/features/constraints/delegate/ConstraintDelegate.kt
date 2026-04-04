package com.tomtruyen.automation.features.constraints.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig

interface ConstraintDelegate<T: ConstraintConfig> {
    val type: ConstraintType
    suspend fun evaluate(config: T, event: AutomationEvent): Boolean
}