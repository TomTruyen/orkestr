package com.tomtruyen.automation.features.actions

import android.content.Context
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.delegate.ActionDelegate
import com.tomtruyen.automation.generated.GeneratedActionProvider
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ActionExecutor(
    context: Context,
    delegates: List<ActionDelegate<out ActionConfig>> = GeneratedActionProvider.delegates(context),
    private val logger: AutomationLogger? = null,
) {
    private val delegatesByType = delegates.associateBy { it.type }

    suspend fun executeAll(
        actions: List<ActionConfig>,
        event: AutomationEvent,
        executionMode: ActionExecutionMode = ActionExecutionMode.PARALLEL,
    ) {
        when (executionMode) {
            ActionExecutionMode.SEQUENTIAL -> {
                actions.forEach { action ->
                    delegatesByType[action.type]?.executeTyped(action, event)
                }
            }

            ActionExecutionMode.PARALLEL -> supervisorScope {
                actions.map { action ->
                    launch {
                        runCatching {
                            delegatesByType[action.type]?.executeTyped(action, event)
                        }.onFailure { error ->
                            logger?.log("Action ${action.type.name} failed during parallel execution", error)
                        }
                    }
                }.joinAll()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private suspend fun ActionDelegate<out ActionConfig>.executeTyped(config: ActionConfig, event: AutomationEvent) {
    (this as ActionDelegate<ActionConfig>).execute(config, event)
}
