package com.tomtruyen.automation.features.triggers.receiver

import android.content.BroadcastReceiver
import android.content.Context
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.AutomationRuntimeService
import kotlinx.coroutines.CoroutineScope

abstract class TriggerReceiver : BroadcastReceiver() {
    protected abstract val service: AutomationRuntimeService
    protected abstract val scope: CoroutineScope
    protected abstract val logger: AutomationLogger

    interface TriggerFactory {
        val key: TriggerReceiverKey

        fun register(
            context: Context,
            service: AutomationRuntimeService,
            scope: CoroutineScope,
            logger: AutomationLogger,
        ): TriggerReceiver
    }
}
