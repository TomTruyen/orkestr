package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.core.model.PhoneVolumeStream
import com.tomtruyen.automation.features.actions.config.SetPhoneVolumeActionConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SetPhoneVolumeActionDefinitionTest {
    @Test
    fun validate_rejectsOutOfRangePercentages() {
        val errors = SetPhoneVolumeActionDefinition.validate(
            SetPhoneVolumeActionConfig(stream = PhoneVolumeStream.MEDIA, levelPercent = 120),
            actionDefinitionTestResolver,
        )

        assertEquals(1, errors.size)
    }
}
