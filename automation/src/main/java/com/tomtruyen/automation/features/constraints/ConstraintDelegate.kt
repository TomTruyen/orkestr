package com.tomtruyen.automation.features.constraints

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig

interface ConstraintDelegate<T: ConstraintConfig> {
    suspend fun evaluate(config: T, event: AutomationEvent): Boolean
}