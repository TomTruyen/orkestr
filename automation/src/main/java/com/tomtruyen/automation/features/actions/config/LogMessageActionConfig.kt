package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(LogMessageActionConfig.DISCRIMINATOR)
data class LogMessageActionConfig(val message: String = "Rule executed") : ActionConfig {
    override val type: ActionType = ActionType.LOG_MESSAGE

    companion object {
        const val DISCRIMINATOR = "log_message"
    }
}
