package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.PowerConnectionEvent
import com.tomtruyen.automation.core.model.PowerConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PowerConnectionReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
) : TriggerReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> PowerConnectionState.CONNECTED
            Intent.ACTION_POWER_DISCONNECTED -> PowerConnectionState.DISCONNECTED
            else -> return
        }
        logger.info("Received power connection event ${state.name}")
        scope.launch {
            service.handleEvent(PowerConnectionEvent(state))
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.POWER_CONNECTION

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = PowerConnectionReceiver(service, scope, logger)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter().apply {
                    addAction(Intent.ACTION_POWER_CONNECTED)
                    addAction(Intent.ACTION_POWER_DISCONNECTED)
                },
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            return receiver
        }
    }
}
