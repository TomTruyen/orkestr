package com.tomtruyen.automation.features.actions

import android.content.Context
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.delegate.ActionDelegate
import com.tomtruyen.automation.features.actions.delegate.LogMessageActionDelegate
import com.tomtruyen.automation.features.actions.delegate.ShowNotificationActionDelegate

class ActionExecutor(
    context: Context,
    delegates: List<ActionDelegate<out ActionConfig>> = listOf(
        LogMessageActionDelegate(),
        ShowNotificationActionDelegate(context)
    )
) {
    private val delegatesByType = delegates.associateBy { it.type }

    suspend fun executeAll(actions: List<ActionConfig>, event: AutomationEvent) {
        actions.forEach {
            delegatesByType[it.type]?.executeTyped(it, event)
        }
    }
}

@Suppress("UNCHECKED_CAST")
private suspend fun ActionDelegate<out ActionConfig>.executeTyped(
    config: ActionConfig,
    event: AutomationEvent
) {
    (this as ActionDelegate<ActionConfig>).execute(config, event)
}
