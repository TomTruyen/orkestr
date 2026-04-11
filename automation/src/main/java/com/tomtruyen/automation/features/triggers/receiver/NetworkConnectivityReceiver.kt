package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.NetworkConnectivityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NetworkConnectivityReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val connectivityManager: ConnectivityManager?,
    private val isConnectedProvider: () -> Boolean,
) : TriggerReceiver() {
    private var lastConnected: Boolean = isConnectedProvider()
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = emitIfChanged()
        override fun onLost(network: Network) = emitIfChanged()
    }

    override fun onReceive(context: Context, intent: Intent) = Unit

    internal fun start() {
        connectivityManager?.registerDefaultNetworkCallback(callback)
    }

    override fun onUnregister(context: Context) {
        runCatching { connectivityManager?.unregisterNetworkCallback(callback) }
    }

    internal fun emitIfChanged() {
        val connected = isConnectedProvider()
        if (connected == lastConnected) return
        lastConnected = connected
        logger.info("Received network connectivity event connected=$connected")
        scope.launch {
            service.handleEvent(NetworkConnectivityEvent(connected))
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.NETWORK_CONNECTIVITY

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val receiver = NetworkConnectivityReceiver(
                service = service,
                scope = scope,
                logger = logger,
                connectivityManager = connectivityManager,
                isConnectedProvider = { connectivityManager?.activeNetwork != null },
            )
            receiver.start()
            return receiver
        }
    }
}
