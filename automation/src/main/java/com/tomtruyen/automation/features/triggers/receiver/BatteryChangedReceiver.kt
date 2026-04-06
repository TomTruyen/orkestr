package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import com.tomtruyen.automation.core.model.BatteryChargeState
import com.tomtruyen.automation.core.model.BatteryPlugStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BatteryChangedReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
) : TriggerReceiver() {
    private var lastState: BatteryStateSnapshot? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BATTERY_CHANGED) return

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val rawStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val rawPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

        val chargeState = BatteryChargeState.fromBatteryManagerState(rawStatus)
        val plugStatus = BatteryPlugStatus.fromBatteryManagerPluggedStatus(rawPlugged)
        val currentState = BatteryStateSnapshot(
            level = level,
            scale = scale,
            chargeState = chargeState,
            plugStatus = plugStatus,
        )
        val previousState = lastState
        lastState = currentState

        if (previousState == null) {
            logger.log(
                """
                    Ignoring initial battery snapshot:
                    level=$level
                    scale=$scale
                    chargeState=$chargeState
                    plugStatus=$plugStatus
                    rawStatus=$rawStatus
                    rawPlugged=$rawPlugged
                """.trimIndent(),
            )
            return
        }

        logger.log(
            """
                Received battery change event: 
                level=$level
                scale=$scale
                chargeState=$chargeState
                plugStatus=$plugStatus
                rawStatus=$rawStatus
                rawPlugged=$rawPlugged
            """.trimIndent(),
        )

        scope.launch {
            service.handleEvent(
                BatteryChangedEvent(
                    level = level,
                    scale = scale,
                    chargeState = chargeState,
                    plugStatus = plugStatus,
                    previousLevel = previousState.level,
                    previousScale = previousState.scale,
                    previousChargeState = previousState.chargeState,
                    previousPlugStatus = previousState.plugStatus,
                ),
            )
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.BATTERY_CHANGED

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = BatteryChangedReceiver(service, scope, logger)

            Log.d("@@@", "Register battery receiver")

            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )

            return receiver
        }
    }
}

private data class BatteryStateSnapshot(
    val level: Int,
    val scale: Int,
    val chargeState: BatteryChargeState,
    val plugStatus: BatteryPlugStatus,
)
