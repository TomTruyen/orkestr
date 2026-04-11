package com.tomtruyen.automation.features.triggers.receiver

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.DoNotDisturbModeChangedEvent
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.model.toDoNotDisturbMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DoNotDisturbModeReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val modeProvider: (Context) -> DoNotDisturbMode? = { receiverContext ->
        receiverContext.getSystemService(NotificationManager::class.java)
            ?.currentInterruptionFilter
            ?.toDoNotDisturbMode()
    },
) : TriggerReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED) return
        val mode = runCatching { modeProvider(context) }.getOrElse { error ->
            if (error is SecurityException) null else throw error
        } ?: return
        logger.info("Received Do Not Disturb mode event mode=${mode.name}")
        scope.launch {
            service.handleEvent(DoNotDisturbModeChangedEvent(mode))
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.DO_NOT_DISTURB_MODE

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = DoNotDisturbModeReceiver(service, scope, logger)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            return receiver
        }
    }
}
