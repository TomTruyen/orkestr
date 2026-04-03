package com.tomtruyen.automation.core

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.data.AutomationRuleRepository
import com.tomtruyen.automation.features.actions.ActionExecutor
import com.tomtruyen.automation.features.constraints.ConstraintEvaluator
import com.tomtruyen.automation.features.triggers.TriggerMatcher

class AutomationRuntimeService(
    private val repository: AutomationRuleRepository,
    private val triggerMatcher: TriggerMatcher,
    private val constraintEvaluator: ConstraintEvaluator,
    private val actionExecutor: ActionExecutor,
) {
    suspend fun handleEvent(event: AutomationEvent) {
        repository.getEnabledRules().forEach { rule ->
            if(!triggerMatcher.matches(rule.triggers, event)) return@forEach
            if(!constraintEvaluator.evaluateAll(rule.constraints, event)) return@forEach

            actionExecutor.executeAll(rule.actions, event)
        }
    }
}