package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
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
}
