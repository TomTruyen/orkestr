package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.features.constraints.config.DateOfMonthConstraintConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class DateOfMonthConstraintDefinitionTest {
    @Test
    fun validate_requiresAtLeastOneDay() {
        val errors = DateOfMonthConstraintDefinition.validate(
            DateOfMonthConstraintConfig(days = emptySet()),
            constraintDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
