package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.core.definition.AutomationTextResolver
import com.tomtruyen.automation.core.model.PhoneVolumeStream
import com.tomtruyen.automation.features.actions.config.FlashTorchActionConfig
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.actions.config.OpenWebsiteActionConfig
import com.tomtruyen.automation.features.actions.config.SetPhoneVolumeActionConfig
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NewActionDefinitionValidationTest {
    private val resolver = object : AutomationTextResolver {
        override fun resolve(stringRes: Int, formatArgs: List<Any>): String =
            "res:$stringRes" + if (formatArgs.isEmpty()) "" else ":" + formatArgs.joinToString()
    }

    @Test
    fun launchApplication_requiresPackageName() {
        val errors = LaunchApplicationActionDefinition.validate(LaunchApplicationActionConfig(), resolver)

        assertEquals(1, errors.size)
    }

    @Test
    fun openWebsite_requiresUrl() {
        val errors = OpenWebsiteActionDefinition.validate(OpenWebsiteActionConfig(), resolver)

        assertEquals(1, errors.size)
    }

    @Test
    fun setWallpaper_requiresImageUri() {
        val errors = SetWallpaperActionDefinition.validate(SetWallpaperActionConfig(), resolver)

        assertEquals(1, errors.size)
    }

    @Test
    fun setPhoneVolume_rejectsOutOfRangePercentages() {
        val errors = SetPhoneVolumeActionDefinition.validate(
            SetPhoneVolumeActionConfig(stream = PhoneVolumeStream.MEDIA, levelPercent = 120),
            resolver,
        )

        assertEquals(1, errors.size)
    }

    @Test
    fun flashTorch_rejectsOutOfRangeTiming() {
        val errors = FlashTorchActionDefinition.validate(
            FlashTorchActionConfig(pulseCount = 0, onDurationMillis = 20, offDurationMillis = 6000),
            resolver,
        )

        assertEquals(3, errors.size)
    }
}
