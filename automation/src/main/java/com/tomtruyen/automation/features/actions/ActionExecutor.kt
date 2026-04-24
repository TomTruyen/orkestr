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
                val (serializedActions, parallelActions) = actions.partition { it.type.requiresSerializedExecutionInParallelMode() }

                buildList {
                    if (serializedActions.isNotEmpty()) {
                        add(
                            launch {
                                serializedActions.forEach { action ->
                                    executeSafely(action, event)
                                }
                            },
                        )
                    }

                    addAll(
                        parallelActions.map { action ->
                            launch {
                                executeSafely(action, event)
                            }
                        },
                    )
                }.joinAll()
            }
        }
    }

    private suspend fun executeSafely(action: ActionConfig, event: AutomationEvent) {
        runCatching {
            delegatesByType[action.type]?.executeTyped(action, event)
        }.onFailure { error ->
            logger?.error("Action ${action.type.name} failed during parallel execution", error)
        }
    }
}

private fun ActionType.requiresSerializedExecutionInParallelMode(): Boolean = when (this) {
    ActionType.DO_NOT_DISTURB,
    ActionType.SET_PHONE_VIBRATE,
    -> true
    else -> false
}

@Suppress("UNCHECKED_CAST")
private suspend fun ActionDelegate<out ActionConfig>.executeTyped(config: ActionConfig, event: AutomationEvent) {
    (this as ActionDelegate<ActionConfig>).execute(config, event)
}
