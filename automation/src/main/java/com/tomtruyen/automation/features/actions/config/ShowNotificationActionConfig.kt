package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(ShowNotificationActionConfig.DISCRIMINATOR)
data class ShowNotificationActionConfig(
    val title: String = "Automation started",
    val message: String = "Your rule was triggered."
) : ActionConfig {
    override val type: ActionType = ActionType.SHOW_NOTIFICATION

    companion object {
        const val DISCRIMINATOR = "show_notification"
    }
}
