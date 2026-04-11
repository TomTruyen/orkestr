package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GeofenceConstraintDefinitionTest {
    @Test
    fun validate_requiresSelectedGeofence() {
        val errors = GeofenceConstraintDefinition.validate(
            GeofenceConstraintConfig(),
            constraintDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
