package com.tomtruyen.automation.features.actions.delegate

import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.AutomationLogger
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.config.LogMessageSeverity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@GenerateActionDelegate
class LogMessageActionDelegate :
    ActionDelegate<LogMessageActionConfig>,
    KoinComponent {
    override val type: ActionType = ActionType.LOG_MESSAGE
    private val logger by inject<AutomationLogger>()

    override suspend fun execute(config: LogMessageActionConfig, event: AutomationEvent) {
        when (config.severity) {
            LogMessageSeverity.DEBUG -> logger.debug(config.message)
            LogMessageSeverity.INFO -> logger.info(config.message)
            LogMessageSeverity.WARNING -> logger.warning(config.message)
            LogMessageSeverity.ERROR -> logger.error(config.message)
        }
    }
}
