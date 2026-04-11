package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.core.model.AutomationLocalTime
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

internal class TimeOfDayConstraintConfigTest {
    @Test
    fun matches_matchesTimesAcrossMidnight() {
        val config = TimeOfDayConstraintConfig(
            startTime = AutomationLocalTime(hour = 22, minute = 0),
            endTime = AutomationLocalTime(hour = 6, minute = 0),
        )

        assertEquals(true, config.matches(LocalTime.of(23, 30)))
        assertEquals(true, config.matches(LocalTime.of(5, 45)))
        assertEquals(false, config.matches(LocalTime.of(12, 0)))
    }
}
