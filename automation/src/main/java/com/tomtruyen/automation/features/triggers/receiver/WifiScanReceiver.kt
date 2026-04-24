package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.WifiScanResultEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private fun String?.normalizeSsid(): String = this.orEmpty().trim().removeSurrounding("\"")

class WifiScanReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val visibleSsidsProvider: (Context) -> Set<String> = { receiverContext ->
        receiverContext.getSystemService(WifiManager::class.java)
            ?.scanResults
            .orEmpty()
            .map { it.SSID.normalizeSsid() }
            .filter(String::isNotBlank)
            .toSet()
    },
    private val connectedSsidProvider: (Context) -> String? = { receiverContext ->
        receiverContext.getSystemService(WifiManager::class.java)
            ?.connectionInfo
            ?.ssid
            ?.normalizeSsid()
            ?.takeIf(String::isNotBlank)
    },
) : TriggerReceiver() {
    private var lastSnapshot: WifiSnapshot? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in ACTIONS) return
        val visibleSsids = runCatching { visibleSsidsProvider(context) }.getOrElse { error ->
            if (error is SecurityException) emptySet() else throw error
        }
        val connectedSsid = runCatching { connectedSsidProvider(context) }.getOrElse { error ->
            if (error is SecurityException) null else throw error
        }
        logger.debug("Received wifi scan event connected=$connectedSsid visible=${visibleSsids.joinToString()}")
        val previousSnapshot = lastSnapshot
        lastSnapshot = WifiSnapshot(
            visibleSsids = visibleSsids,
            connectedSsid = connectedSsid,
        )
        scope.launch {
            service.handleEvent(
                WifiScanResultEvent(
                    visibleSsids = visibleSsids,
                    connectedSsid = connectedSsid,
                    previousVisibleSsids = previousSnapshot?.visibleSsids,
                    previousConnectedSsid = previousSnapshot?.connectedSsid,
                ),
            )
        }
    }

    private data class WifiSnapshot(val visibleSsids: Set<String>, val connectedSsid: String?)

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.WIFI_SCAN

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = WifiScanReceiver(service, scope, logger)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter().apply {
                    ACTIONS.forEach(::addAction)
                },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            return receiver
        }

        private val ACTIONS = setOf(
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION,
            WifiManager.NETWORK_STATE_CHANGED_ACTION,
        )
    }
}
