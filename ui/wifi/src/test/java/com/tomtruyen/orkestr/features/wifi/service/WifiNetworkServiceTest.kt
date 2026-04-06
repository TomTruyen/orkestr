package com.tomtruyen.orkestr.features.wifi.service

import org.junit.Assert.assertEquals
import org.junit.Test

internal class WifiNetworkServiceTest {
    @Test
    fun `loadAvailableNetworks normalizes and deduplicates ssids`() {
        val service = WifiNetworkService(
            currentSsidProvider = { "\"Home WiFi\"" },
            scannedSsidsProvider = {
                listOf("Office", "\"Home WiFi\"", " ", "Office")
            },
        )

        val snapshot = service.loadAvailableNetworks()

        assertEquals("Home WiFi", snapshot.currentSsid)
        assertEquals(listOf("Home WiFi", "Office"), snapshot.discoveredSsids)
    }

    @Test
    fun `refreshAvailableNetworks ignores security exception from scan`() {
        val service = WifiNetworkService(
            currentSsidProvider = { null },
            scannedSsidsProvider = { listOf("Cafe") },
            scanTrigger = { throw SecurityException("denied") },
        )

        val snapshot = service.refreshAvailableNetworks()

        assertEquals(listOf("Cafe"), snapshot.discoveredSsids)
    }

    @Test
    fun `loadAvailableNetworks returns empty results when providers throw security exception`() {
        val service = WifiNetworkService(
            currentSsidProvider = { throw SecurityException("denied") },
            scannedSsidsProvider = { throw SecurityException("denied") },
        )

        val snapshot = service.loadAvailableNetworks()

        assertEquals(null, snapshot.currentSsid)
        assertEquals(emptyList<String>(), snapshot.discoveredSsids)
    }
}
