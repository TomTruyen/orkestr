package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.BatterySaverStateChangedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BatterySaverModeReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val isPowerSaveModeProvider: (Context) -> Boolean = { receiverContext ->
        receiverContext.getSystemService(PowerManager::class.java)?.isPowerSaveMode == true
    },
) : TriggerReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) return
        val enabled = isPowerSaveModeProvider(context)
        logger.info("Received battery saver state event enabled=$enabled")
        scope.launch {
            service.handleEvent(BatterySaverStateChangedEvent(enabled))
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.BATTERY_SAVER

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = BatterySaverModeReceiver(service, scope, logger)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            return receiver
        }
    }
}
