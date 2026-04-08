package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.model.AutomationLocalTime
import com.tomtruyen.automation.features.constraints.config.DateOfMonthConstraintConfig
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NewConstraintDefinitionValidationTest {
    private val resolver = object : AutomationTextResolver {
        override fun resolve(stringRes: Int, formatArgs: List<Any>): String =
            "res:$stringRes" + if (formatArgs.isEmpty()) "" else ":" + formatArgs.joinToString()
    }

    @Test
    fun dateOfMonth_requiresAtLeastOneDay() {
        val errors = DateOfMonthConstraintDefinition.validate(DateOfMonthConstraintConfig(days = emptySet()), resolver)

        assertEquals(1, errors.size)
    }

    @Test
    fun timeOfDay_matchesTimesAcrossMidnight() {
        val config = TimeOfDayConstraintConfig(
            startTime = AutomationLocalTime(hour = 22, minute = 0),
            endTime = AutomationLocalTime(hour = 6, minute = 0),
        )

        assertEquals(true, config.matches(java.time.LocalTime.of(23, 30)))
        assertEquals(true, config.matches(java.time.LocalTime.of(5, 45)))
        assertEquals(false, config.matches(java.time.LocalTime.of(12, 0)))
    }

    @Test
    fun geofence_requiresSelectedGeofence() {
        val errors = GeofenceConstraintDefinition.validate(GeofenceConstraintConfig(), resolver)

        assertEquals(1, errors.size)
    }

    @Test
    fun wifiSsid_requiresSelectedSsid() {
        val errors = WifiSsidConstraintDefinition.validate(WifiSsidConstraintConfig(), resolver)

        assertEquals(1, errors.size)
    }
}
