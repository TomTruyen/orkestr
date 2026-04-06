package com.tomtruyen.orkestr.features.automation.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class WifiNetworkServiceTest {
    @Test
    fun loadAvailableNetworks_normalizesAndSortsSsids() {
        val service = WifiNetworkService(
            currentSsidProvider = { "\"Office\"" },
            scannedSsidsProvider = { listOf(" Guest ", "\"Office\"", "") },
        )

        assertEquals(
            WifiNetworksSnapshot(
                currentSsid = "Office",
                discoveredSsids = listOf("Guest", "Office"),
            ),
            service.loadAvailableNetworks(),
        )
    }

    @Test
    fun refreshAvailableNetworks_whenScanThrowsSecurityException_stillReturnsKnownNetworks() {
        val service = WifiNetworkService(
            currentSsidProvider = { "\"Office\"" },
            scannedSsidsProvider = { listOf("Office") },
            scanTrigger = { throw SecurityException("missing nearby devices permission") },
        )

        assertEquals(
            WifiNetworksSnapshot(
                currentSsid = "Office",
                discoveredSsids = listOf("Office"),
            ),
            service.refreshAvailableNetworks(),
        )
    }

    @Test
    fun loadAvailableNetworks_whenProvidersThrowSecurityException_returnsEmptySnapshot() {
        val service = WifiNetworkService(
            currentSsidProvider = { throw SecurityException("blocked") },
            scannedSsidsProvider = { throw SecurityException("blocked") },
        )

        val snapshot = service.loadAvailableNetworks()

        assertNull(snapshot.currentSsid)
        assertEquals(emptyList<String>(), snapshot.discoveredSsids)
    }
}
