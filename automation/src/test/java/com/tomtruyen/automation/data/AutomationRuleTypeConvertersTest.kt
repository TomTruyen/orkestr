package com.tomtruyen.automation.data

import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.model.PowerConnectionState
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.actions.config.DoNotDisturbActionConfig
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.triggers.config.BatteryChangedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.BatteryLevelTriggerConfig
import com.tomtruyen.automation.features.triggers.config.PowerConnectionTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationRuleTypeConvertersTest {
    private val converters = AutomationRuleTypeConverters()

    @Test
    fun triggers_roundTripThroughJson() {
        val triggers = listOf(
            BatteryChangedTriggerConfig(state = BatteryChargeState.FULL),
            BatteryLevelTriggerConfig(
                operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
                value = 15,
            ),
            PowerConnectionTriggerConfig(state = PowerConnectionState.DISCONNECTED),
            TimeBasedTriggerConfig(
                hour = 8,
                minute = 30,
                days = setOf(Weekday.MONDAY, Weekday.WEDNESDAY),
            ),
        )

        val encoded = converters.fromTriggers(triggers)
        val decoded = converters.toTriggers(encoded)

        assertEquals(triggers, decoded)
    }

    @Test
    fun triggers_whenInputIsBlank_returnsEmptyList() {
        assertEquals(emptyList<Any>(), converters.toTriggers("   "))
    }

    @Test
    fun triggers_whenInputIsInvalid_returnsEmptyList() {
        assertEquals(emptyList<Any>(), converters.toTriggers("{invalid"))
    }

    @Test
    fun constraints_roundTripThroughJson() {
        val constraints = listOf(
            BatteryLevelConstraintConfig(
                operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
                value = 40,
            ),
        )

        val encoded = converters.fromConstraints(constraints)
        val decoded = converters.toConstraints(encoded)

        assertEquals(constraints, decoded)
    }

    @Test
    fun constraints_whenInputIsInvalid_returnsEmptyList() {
        assertEquals(emptyList<Any>(), converters.toConstraints("not-json"))
    }

    @Test
    fun constraintGroups_roundTripThroughJson() {
        val groups = listOf(
            ConstraintGroup(
                constraints = listOf(
                    BatteryLevelConstraintConfig(
                        operator = ComparisonOperator.GREATER_THAN_OR_EQUAL,
                        value = 70,
                    ),
                ),
            ),
            ConstraintGroup(
                constraints = listOf(
                    BatteryLevelConstraintConfig(
                        operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
                        value = 30,
                    ),
                ),
            ),
        )

        val encoded = converters.fromConstraintGroups(groups)
        val decoded = converters.toConstraintGroups(encoded)

        assertEquals(groups, decoded)
    }

    @Test
    fun constraintGroups_whenInputIsInvalid_returnsEmptyList() {
        assertEquals(emptyList<Any>(), converters.toConstraintGroups("not-json"))
    }

    @Test
    fun actions_roundTripThroughJson() {
        val actions = listOf(DoNotDisturbActionConfig(mode = DoNotDisturbMode.ALARMS_ONLY))

        val encoded = converters.fromActions(actions)
        val decoded = converters.toActions(encoded)

        assertEquals(actions, decoded)
    }

    @Test
    fun actions_whenInputIsBlank_returnsEmptyList() {
        assertEquals(emptyList<Any>(), converters.toActions(""))
    }
}
