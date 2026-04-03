package com.tomtruyen.automation.features.constraints

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig

class ConstraintEvaluator(
    private val delegates: Map<ConstraintType, ConstraintDelegate<ConstraintConfig>>
) {
    suspend fun evaluateAll(constraints: List<ConstraintConfig>, event: AutomationEvent): Boolean {
        return constraints.any { constraint ->
            delegates[constraint.type]?.evaluate(constraint, event) ?: false
        }
    }
}