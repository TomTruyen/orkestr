package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class LaunchApplicationActionDefinitionTest {
    @Test
    fun validate_requiresPackageName() {
        val errors = LaunchApplicationActionDefinition.validate(
            LaunchApplicationActionConfig(),
            actionDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
