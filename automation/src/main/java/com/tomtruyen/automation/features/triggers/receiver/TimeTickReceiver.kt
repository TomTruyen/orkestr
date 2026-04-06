package com.tomtruyen.automation.features.triggers.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.tomtruyen.automation.codegen.GenerateReceiverFactory
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import com.tomtruyen.automation.core.event.TimeBasedEvent
import com.tomtruyen.automation.core.model.Weekday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TimeTickReceiver(
    override val service: AutomationRuntimeService,
    override val scope: CoroutineScope,
    override val logger: AutomationLogger,
    private val nowProvider: () -> LocalDateTime = { LocalDateTime.now() },
) : TriggerReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in ACTIONS) return
        val now = nowProvider()
        logger.log("Received time tick event for ${now.hour}:${now.minute}")
        scope.launch {
            service.handleEvent(
                TimeBasedEvent(
                    hour = now.hour,
                    minute = now.minute,
                    day = Weekday.from(now.dayOfWeek),
                ),
            )
        }
    }

    @GenerateReceiverFactory
    companion object Factory : TriggerFactory {
        override val key: TriggerReceiverKey = TriggerReceiverKey.TIME_TICK

        override fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver {
            val receiver = TimeTickReceiver(service, scope, logger)
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
            Intent.ACTION_TIME_TICK,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
