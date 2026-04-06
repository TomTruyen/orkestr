package com.tomtruyen.automation.core

import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.ManualAutomationEvent
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
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
            executeRule(rule, event)
        }
    }

    suspend fun runRuleNow(rule: AutomationRule) {
        executeRule(
            rule = rule,
            event = ManualAutomationEvent(ruleId = rule.id),
            ignoreTriggers = true,
        )
    }

    private suspend fun executeRule(rule: AutomationRule, event: AutomationEvent, ignoreTriggers: Boolean = false) {
        if (!ignoreTriggers && !triggerMatcher.matches(rule.triggers, event)) return
        if (!constraintEvaluator.evaluateAll(rule.constraints, event)) return

        actionExecutor.executeAll(rule.actions, event)
    }
}
