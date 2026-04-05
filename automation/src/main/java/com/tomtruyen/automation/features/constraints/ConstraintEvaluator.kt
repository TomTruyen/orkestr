package com.tomtruyen.automation.features.constraints

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.constraints.delegate.ConstraintDelegate
import com.tomtruyen.automation.generated.GeneratedConstraintProvider

class ConstraintEvaluator(
    delegates: List<ConstraintDelegate<out ConstraintConfig>> = GeneratedConstraintProvider.delegates(),
) {
    private val delegatesByType = delegates.associateBy { it.type }

    suspend fun evaluateAll(constraints: List<ConstraintConfig>, event: AutomationEvent): Boolean {
        if (constraints.isEmpty()) return true

        return constraints.all { constraint ->
            delegatesByType[constraint.type]?.evaluateTyped(constraint, event) ?: false
        }
    }
}

@Suppress("UNCHECKED_CAST")
private suspend fun ConstraintDelegate<out ConstraintConfig>.evaluateTyped(
    config: ConstraintConfig,
    event: AutomationEvent,
): Boolean = (this as ConstraintDelegate<ConstraintConfig>).evaluate(config, event)
