package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.orkestr.features.automation.viewmodel.withConstraintConditionGroup
import com.tomtruyen.orkestr.features.automation.viewmodel.withConstraintConditionGroupDeleted
import com.tomtruyen.orkestr.features.automation.viewmodel.withConstraintConditionGroupUpdated
import com.tomtruyen.orkestr.features.automation.viewmodel.withConstraintInConditionGroup
import com.tomtruyen.orkestr.features.automation.viewmodel.withConstraintRemovedFromConditionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationGroupEditorModelsTest {
    @Test
    fun withNodeAdded_addsTriggerToTriggerGroup() {
        val group = AutomationNodeGroup(
            id = "group",
            name = "Triggers",
            type = AutomationNodeGroupType.TRIGGER,
        )
        val trigger = BatteryChangedTriggerConfig()

        val updated = group.withNodeAdded(trigger)

        assertEquals(listOf(trigger), updated.triggers)
        assertEquals(1, updated.nodeCount())
    }

    @Test
    fun withNodeAdded_ignoresConfigFromDifferentGroupType() {
        val group = AutomationNodeGroup(
            id = "group",
            name = "Triggers",
            type = AutomationNodeGroupType.TRIGGER,
        )

        val updated = group.withNodeAdded(LogMessageActionConfig())

        assertEquals(group, updated)
    }

    @Test
    fun withNodeRemovedAt_removesNodeForMatchingGroupType() {
        val remaining = LogMessageActionConfig(message = "remaining")
        val group = AutomationNodeGroup(
            id = "group",
            name = "Actions",
            type = AutomationNodeGroupType.ACTION,
            actions = listOf(LogMessageActionConfig(message = "removed"), remaining),
        )

        val updated = group.withNodeRemovedAt(0)

        assertEquals(listOf(remaining), updated.actions)
        assertEquals(1, updated.nodeCount())
    }

    @Test
    fun nodeCount_usesActiveGroupTypeOnly() {
        val group = AutomationNodeGroup(
            id = "group",
            name = "Constraints",
            type = AutomationNodeGroupType.CONSTRAINT,
            triggers = listOf(BatteryChangedTriggerConfig()),
            constraints = listOf(BatteryLevelConstraintConfig(), BatteryLevelConstraintConfig(value = 80)),
            actions = listOf(LogMessageActionConfig()),
        )

        assertEquals(2, group.nodeCount())
    }

    @Test
    fun withConstraintConditionGroup_groupsSelectedConstraintsAndKeepsUnselectedAsAlternatives() {
        val first = BatteryLevelConstraintConfig(value = 20)
        val second = BatteryLevelConstraintConfig(value = 40)
        val third = BatteryLevelConstraintConfig(value = 80)
        val state = RuleEditorState(
            id = "rule",
            constraints = listOf(first, second, third),
        )

        val updated = state.withConstraintConditionGroup(setOf(0, 2))

        assertEquals(listOf(first, third), updated.constraintGroups[0].constraints)
        assertEquals(listOf(second), updated.constraintGroups[1].constraints)
    }

    @Test
    fun withConstraintInConditionGroup_appendsConstraintToSelectedGroup() {
        val first = BatteryLevelConstraintConfig(value = 20)
        val second = BatteryLevelConstraintConfig(value = 40)
        val third = BatteryLevelConstraintConfig(value = 80)
        val state = RuleEditorState(
            id = "rule",
            constraints = listOf(first, second),
        ).withConstraintConditionGroup(setOf(0, 1))

        val updated = state.withConstraintInConditionGroup(third, groupIndex = 0)

        assertEquals(listOf(first, second, third), updated.constraintGroups[0].constraints)
        assertEquals(listOf(first, second, third), updated.constraints)
    }

    @Test
    fun withConstraintConditionGroupUpdated_replacesSelectedGroupMembership() {
        val first = BatteryLevelConstraintConfig(value = 20)
        val second = BatteryLevelConstraintConfig(value = 40)
        val third = BatteryLevelConstraintConfig(value = 80)
        val state = RuleEditorState(
            id = "rule",
            constraints = listOf(first, second, third),
            constraintGroups = listOf(
                ConstraintGroup(listOf(first, second)),
                ConstraintGroup(listOf(third)),
            ),
        )

        val updated = state.withConstraintConditionGroupUpdated(groupIndex = 0, indices = setOf(1, 2))

        assertEquals(listOf(second, third), updated.constraintGroups[0].constraints)
        assertEquals(listOf(third), updated.constraintGroups[1].constraints)
        assertEquals(listOf(first, second, third), updated.constraints)
    }

    @Test
    fun withConstraintConditionGroupDeleted_removesGroupWithoutDeletingConstraints() {
        val first = BatteryLevelConstraintConfig(value = 20)
        val second = BatteryLevelConstraintConfig(value = 40)
        val state = RuleEditorState(
            id = "rule",
            constraints = listOf(first, second),
            constraintGroups = listOf(
                ConstraintGroup(listOf(first)),
                ConstraintGroup(listOf(second)),
            ),
        )

        val updated = state.withConstraintConditionGroupDeleted(groupIndex = 0)

        assertEquals(listOf(ConstraintGroup(listOf(second))), updated.constraintGroups)
        assertEquals(listOf(first, second), updated.constraints)
    }

    @Test
    fun withConstraintRemovedFromConditionGroup_removesMembershipWithoutDeletingConstraint() {
        val first = BatteryLevelConstraintConfig(value = 20)
        val second = BatteryLevelConstraintConfig(value = 40)
        val state = RuleEditorState(
            id = "rule",
            constraints = listOf(first, second),
            constraintGroups = listOf(ConstraintGroup(listOf(first, second))),
        )

        val updated = state.withConstraintRemovedFromConditionGroup(groupIndex = 0, constraintIndex = 0)

        assertEquals(listOf(ConstraintGroup(listOf(second))), updated.constraintGroups)
        assertEquals(listOf(first, second), updated.constraints)
    }
}
