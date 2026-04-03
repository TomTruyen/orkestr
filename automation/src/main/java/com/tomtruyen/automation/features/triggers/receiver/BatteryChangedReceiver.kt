package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.BatteryChangedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BatteryChangedReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger
): TriggerReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action != Intent.ACTION_BATTERY_CHANGED) return

        logger.log(
            """
                Received battery change event: 
                level=${intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)}
                scale=${intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)}
                status=${intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)}
                plugged=${intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)}
            """.trimIndent()
        )

        scope.launch {
            service.handleEvent(
                BatteryChangedEvent(
                    level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                    scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                    status = intent.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN
                    ),
                    plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0),
                )
            )
        }
    }

    companion object Factory: TriggerFactory {
        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger
        ): TriggerReceiver {
            val receiver = BatteryChangedReceiver(service, scope, logger)

            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            return receiver
        }
    }
}