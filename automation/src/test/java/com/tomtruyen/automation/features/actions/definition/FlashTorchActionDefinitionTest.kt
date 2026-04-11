package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.features.actions.config.FlashTorchActionConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FlashTorchActionDefinitionTest {
    @Test
    fun validate_rejectsOutOfRangeTiming() {
        val errors = FlashTorchActionDefinition.validate(
            FlashTorchActionConfig(pulseCount = 0, onDurationMillis = 20, offDurationMillis = 6000),
            actionDefinitionTestResolver,
        )

        assertEquals(3, errors.size)
    }
}
