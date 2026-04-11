package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class WifiSsidConstraintDefinitionTest {
    @Test
    fun validate_requiresSelectedSsid() {
        val errors = WifiSsidConstraintDefinition.validate(
            WifiSsidConstraintConfig(),
            constraintDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
