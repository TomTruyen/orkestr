package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.features.actions.config.OpenWebsiteActionConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class OpenWebsiteActionDefinitionTest {
    @Test
    fun validate_requiresUrl() {
        val errors = OpenWebsiteActionDefinition.validate(
            OpenWebsiteActionConfig(),
            actionDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
