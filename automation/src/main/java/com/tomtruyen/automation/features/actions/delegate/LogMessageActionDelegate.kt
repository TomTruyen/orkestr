package com.tomtruyen.automation.features.actions.delegate

import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig

@GenerateActionDelegate
class LogMessageActionDelegate: ActionDelegate<LogMessageActionConfig> {
    override val type: ActionType = ActionType.LOG_MESSAGE

    override suspend fun execute(config: LogMessageActionConfig, event: AutomationEvent) {
        // TODO: Log to Room Database Table
        println(config.message)
    }
}
