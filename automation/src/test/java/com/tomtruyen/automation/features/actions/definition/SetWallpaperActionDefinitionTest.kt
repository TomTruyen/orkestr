package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SetWallpaperActionDefinitionTest {
    @Test
    fun validate_requiresImageUri() {
        val errors = SetWallpaperActionDefinition.validate(
            SetWallpaperActionConfig(),
            actionDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
