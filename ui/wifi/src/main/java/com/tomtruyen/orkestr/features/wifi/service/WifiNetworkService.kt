package com.tomtruyen.orkestr.features.wifi.service

import android.net.wifi.WifiManager

data class WifiNetworksSnapshot(val currentSsid: String?, val discoveredSsids: List<String>)

class WifiNetworkService(
    private val currentSsidProvider: () -> String?,
    private val scannedSsidsProvider: () -> List<String>,
    private val scanTrigger: () -> Unit = {},
) {
    fun loadAvailableNetworks(): WifiNetworksSnapshot = WifiNetworksSnapshot(
        currentSsid = loadCurrentSsid(),
        discoveredSsids = loadDiscoveredSsids(),
    )

    fun refreshAvailableNetworks(): WifiNetworksSnapshot {
        runCatching(scanTrigger).getOrElse { error ->
            if (error !is SecurityException) throw error
        }
        return loadAvailableNetworks()
    }

    private fun loadCurrentSsid(): String? = runCatching(currentSsidProvider).getOrElse { error ->
        if (error is SecurityException) null else throw error
    }?.normalizeSsid()?.takeIf(String::isNotBlank)

    private fun loadDiscoveredSsids(): List<String> = runCatching(scannedSsidsProvider).getOrElse { error ->
        if (error is SecurityException) emptyList() else throw error
    }
        .map(String::normalizeSsid)
        .filter(String::isNotBlank)
        .distinct()
        .sorted()

    companion object {
        fun create(wifiManager: WifiManager?): WifiNetworkService = WifiNetworkService(
            currentSsidProvider = {
                wifiManager?.connectionInfo?.ssid.normalizeSsid()
            },
            scannedSsidsProvider = {
                wifiManager?.scanResults
                    .orEmpty()
                    .map { it.SSID.normalizeSsid() }
            },
            scanTrigger = {
                @Suppress("DEPRECATION")
                wifiManager?.startScan()
            },
        )
    }
}

private fun String?.normalizeSsid(): String = this.orEmpty().removeSurrounding("\"").trim()
