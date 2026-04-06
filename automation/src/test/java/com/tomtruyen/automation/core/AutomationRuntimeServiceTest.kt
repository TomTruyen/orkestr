package com.tomtruyen.automation.core

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.event.ManualAutomationEvent
import com.tomtruyen.automation.data.repository.AutomationRuleRepository
import com.tomtruyen.automation.features.actions.ActionExecutor
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintEvaluator
import com.tomtruyen.automation.features.triggers.TriggerMatcher
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AutomationRuntimeServiceTest {
    @MockK
    private lateinit var repository: AutomationRuleRepository

    @MockK
    private lateinit var triggerMatcher: TriggerMatcher

    @MockK
    private lateinit var constraintEvaluator: ConstraintEvaluator

    @MockK
    private lateinit var actionExecutor: ActionExecutor

    private lateinit var service: AutomationRuntimeService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        service = AutomationRuntimeService(
            repository = repository,
            triggerMatcher = triggerMatcher,
            constraintEvaluator = constraintEvaluator,
            actionExecutor = actionExecutor,
        )

        coEvery { actionExecutor.executeAll(any(), any(), any()) } just Runs
    }

    @Test
    fun handleEvent_withNoEnabledRules_doesNotExecuteAnyActions() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()
        coEvery { repository.getEnabledRules() } returns emptyList()

        service.handleEvent(event)

        coVerify(exactly = 0) { triggerMatcher.matches(any(), any()) }
        coVerify(exactly = 0) { constraintEvaluator.evaluateAll(any(), any()) }
        coVerify(exactly = 0) { actionExecutor.executeAll(any(), any(), any()) }
    }

    @Test
    fun handleEvent_whenTriggersDoNotMatch_skipsConstraintsAndActions() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()
        val rule = mockk<AutomationRule>()

        coEvery { repository.getEnabledRules() } returns listOf(rule)
        coEvery { triggerMatcher.matches(rule.triggers, event) } returns false

        service.handleEvent(event)

        coVerify { triggerMatcher.matches(rule.triggers, event) }
        coVerify(exactly = 0) { constraintEvaluator.evaluateAll(any(), any()) }
        coVerify(exactly = 0) { actionExecutor.executeAll(any(), any(), any()) }
    }

    @Test
    fun handleEvent_whenConstraintsDoNotPass_skipsActions() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()
        val rule = mockk<AutomationRule>()

        coEvery { repository.getEnabledRules() } returns listOf(rule)
        coEvery { triggerMatcher.matches(rule.triggers, event) } returns true
        coEvery { constraintEvaluator.evaluateAll(rule.constraints, event) } returns false

        service.handleEvent(event)

        coVerify { triggerMatcher.matches(rule.triggers, event) }
        coVerify { constraintEvaluator.evaluateAll(rule.constraints, event) }
        coVerify(exactly = 0) { actionExecutor.executeAll(any(), any(), any()) }
    }

    @Test
    fun handleEvent_whenTriggersAndConstraintsPass_executesActions() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()

        val actions = listOf(mockk<ActionConfig>())
        val rule = mockk<AutomationRule> {
            every { this@mockk.actions } returns actions
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.SEQUENTIAL
        }

        coEvery { repository.getEnabledRules() } returns listOf(rule)
        coEvery { triggerMatcher.matches(rule.triggers, event) } returns true
        coEvery { constraintEvaluator.evaluateAll(rule.constraints, event) } returns true

        service.handleEvent(event)

        coVerify { actionExecutor.executeAll(actions, event, ActionExecutionMode.SEQUENTIAL) }
    }

    @Test
    fun handleEvent_withMultipleRules_executesAllMatchingRules() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()

        val actions1 = listOf(mockk<ActionConfig>())
        val rule1 = mockk<AutomationRule> {
            every { this@mockk.actions } returns actions1
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.PARALLEL
        }

        val actions2 = listOf(mockk<ActionConfig>())
        val rule2 = mockk<AutomationRule> {
            every { this@mockk.actions } returns actions2
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.PARALLEL
        }

        val actions3 = listOf(mockk<ActionConfig>())
        val rule3 = mockk<AutomationRule> {
            every { this@mockk.actions } returns actions3
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.PARALLEL
        }

        coEvery { repository.getEnabledRules() } returns listOf(rule1, rule2, rule3)

        // rule1: triggers match, constraints pass -> execute
        coEvery { triggerMatcher.matches(rule1.triggers, event) } returns true
        coEvery { constraintEvaluator.evaluateAll(rule1.constraints, event) } returns true

        // rule2: triggers don't match -> skip
        coEvery { triggerMatcher.matches(rule2.triggers, event) } returns false

        // rule3: triggers match, constraints don't pass -> skip
        coEvery { triggerMatcher.matches(rule3.triggers, event) } returns true
        coEvery { constraintEvaluator.evaluateAll(rule3.constraints, event) } returns false

        service.handleEvent(event)

        coVerify { actionExecutor.executeAll(actions1, event, ActionExecutionMode.PARALLEL) }
        coVerify(exactly = 0) { actionExecutor.executeAll(actions2, event, any()) }
        coVerify(exactly = 0) { actionExecutor.executeAll(actions3, event, any()) }
    }

    @Test
    fun handleEvent_withMultipleRulesAllMatching_executesAllActions() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()

        val actions1 = listOf(mockk<ActionConfig>())
        val rule1 = mockk<AutomationRule>(relaxed = true) {
            every { this@mockk.actions } returns actions1
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.PARALLEL
        }

        val actions2 = listOf(mockk<ActionConfig>())
        val rule2 = mockk<AutomationRule>(relaxed = true) {
            every { this@mockk.actions } returns actions2
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.SEQUENTIAL
        }

        coEvery { repository.getEnabledRules() } returns listOf(rule1, rule2)

        coEvery { triggerMatcher.matches(any(), event) } returns true
        coEvery { constraintEvaluator.evaluateAll(any(), event) } returns true

        service.handleEvent(event)

        coVerify { actionExecutor.executeAll(actions1, event, ActionExecutionMode.PARALLEL) }
        coVerify { actionExecutor.executeAll(actions2, event, ActionExecutionMode.SEQUENTIAL) }
    }

    @Test
    fun handleEvent_whenConstraintsAreEmpty_executesActions() = runTest(StandardTestDispatcher()) {
        val event = mockk<AutomationEvent>()
        val actions = listOf(mockk<ActionConfig>())
        val rule = mockk<AutomationRule> {
            every { this@mockk.actions } returns actions
            every { this@mockk.constraints } returns emptyList()
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.PARALLEL
        }

        coEvery { repository.getEnabledRules() } returns listOf(rule)
        coEvery { triggerMatcher.matches(rule.triggers, event) } returns true
        coEvery { constraintEvaluator.evaluateAll(emptyList(), event) } returns true

        service.handleEvent(event)

        coVerify { actionExecutor.executeAll(actions, event, ActionExecutionMode.PARALLEL) }
    }

    @Test
    fun runRuleNow_skipsTriggerMatchingAndExecutesActionsWhenConstraintsPass() = runTest(StandardTestDispatcher()) {
        val actions = listOf(mockk<ActionConfig>())
        val rule = mockk<AutomationRule> {
            every { this@mockk.id } returns "rule-1"
            every { this@mockk.actions } returns actions
            every { this@mockk.constraints } returns emptyList()
            every { this@mockk.actionExecutionMode } returns ActionExecutionMode.SEQUENTIAL
        }

        coEvery { constraintEvaluator.evaluateAll(emptyList(), any()) } returns true

        service.runRuleNow(rule)

        coVerify(exactly = 0) { triggerMatcher.matches(any(), any()) }
        coVerify {
            constraintEvaluator.evaluateAll(
                emptyList(),
                withArg { event -> assertTrue(event is ManualAutomationEvent && event.ruleId == "rule-1") },
            )
        }
        coVerify {
            actionExecutor.executeAll(
                actions,
                withArg { event -> assertTrue(event is ManualAutomationEvent && event.ruleId == "rule-1") },
                ActionExecutionMode.SEQUENTIAL,
            )
        }
    }

    @Test
    fun runRuleNow_whenConstraintsFail_skipsActions() = runTest(StandardTestDispatcher()) {
        val rule = mockk<AutomationRule> {
            every { this@mockk.id } returns "rule-2"
            every { this@mockk.constraints } returns emptyList()
        }

        coEvery { constraintEvaluator.evaluateAll(emptyList(), any()) } returns false

        service.runRuleNow(rule)

        coVerify(exactly = 0) { triggerMatcher.matches(any(), any()) }
        coVerify(exactly = 0) { actionExecutor.executeAll(any(), any(), any()) }
    }
}
