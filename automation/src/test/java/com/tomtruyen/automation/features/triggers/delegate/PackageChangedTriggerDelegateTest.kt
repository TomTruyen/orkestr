package com.tomtruyen.automation.features.triggers.delegate

import com.tomtruyen.automation.core.event.PackageChangedEvent
import com.tomtruyen.automation.core.model.PackageChangeType
import com.tomtruyen.automation.features.triggers.config.PackageChangedTriggerConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class PackageChangedTriggerDelegateTest {
    private val delegate = PackageChangedTriggerDelegate()
    private val event = PackageChangedEvent("com.spotify.music", PackageChangeType.UPDATED)

    @Test
    fun matches_whenPackageAndChangeTypeMatch_returnsTrue() {
        assertTrue(
            delegate.matches(
                PackageChangedTriggerConfig(
                    packageName = "com.spotify.music",
                    changeType = PackageChangeType.UPDATED,
                ),
                event,
            ),
        )
    }

    @Test
    fun matches_whenPackageNameIsBlank_matchesAnyPackage() {
        assertTrue(delegate.matches(PackageChangedTriggerConfig(changeType = PackageChangeType.UPDATED), event))
    }

    @Test
    fun matches_whenPackageDiffers_returnsFalse() {
        assertFalse(
            delegate.matches(
                PackageChangedTriggerConfig(
                    packageName = "com.whatsapp",
                    changeType = PackageChangeType.UPDATED,
                ),
                event,
            ),
        )
    }

    @Test
    fun matches_whenChangeTypeDiffers_returnsFalse() {
        assertFalse(
            delegate.matches(
                PackageChangedTriggerConfig(
                    packageName = "com.spotify.music",
                    changeType = PackageChangeType.INSTALLED,
                ),
                event,
            ),
        )
    }
}
