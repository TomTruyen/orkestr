package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.AutomationRule
import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

internal class AutomationRulesViewModelTest {
    @Test
    fun copyAsDuplicate_createsNewRuleIdAndKeepsRuleContent() {
        val trigger = BatteryChangedTriggerConfig()
        val constraint = BatteryLevelConstraintConfig(value = 80)
        val action = LogMessageActionConfig(message = "Copied")
        val rule = AutomationRule(
            id = "rule",
            name = "Original",
            enabled = true,
            triggers = listOf(trigger),
            constraints = listOf(constraint),
            constraintGroups = listOf(ConstraintGroup(listOf(constraint))),
            actions = listOf(action),
        )

        val copy = rule.copyAsDuplicate()

        assertNotEquals(rule.id, copy.id)
        assertEquals("Original Copy", copy.name)
        assertEquals(rule.enabled, copy.enabled)
        assertEquals(rule.triggers, copy.triggers)
        assertEquals(rule.constraints, copy.constraints)
        assertEquals(rule.constraintGroups, copy.constraintGroups)
        assertEquals(rule.actions, copy.actions)
        assertEquals(rule.actionExecutionMode, copy.actionExecutionMode)
    }
}
