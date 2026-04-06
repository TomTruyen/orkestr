package com.tomtruyen.orkestr.features.automation.service

import org.junit.Assert.assertEquals
import org.junit.Test

internal class InstalledAppServiceTest {
    @Test
    fun loadInstalledApps_sortsAndDeduplicatesByPackageName() {
        val service = InstalledAppService {
            listOf(
                InstalledAppOption(packageName = "b", label = "Beta"),
                InstalledAppOption(packageName = "a", label = "Alpha"),
                InstalledAppOption(packageName = "a", label = "Alpha Duplicate"),
            )
        }

        assertEquals(
            listOf(
                InstalledAppOption(packageName = "a", label = "Alpha"),
                InstalledAppOption(packageName = "b", label = "Beta"),
            ),
            service.loadInstalledApps(),
        )
    }

    @Test
    fun loadInstalledApps_whenSecurityExceptionThrown_returnsEmptyList() {
        val service = InstalledAppService {
            throw SecurityException("missing package visibility")
        }

        assertEquals(emptyList<InstalledAppOption>(), service.loadInstalledApps())
    }
}
