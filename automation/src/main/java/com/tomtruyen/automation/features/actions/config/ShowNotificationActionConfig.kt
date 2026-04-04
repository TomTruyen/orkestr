package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.PostNotificationPermission
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(ShowNotificationActionConfig.DISCRIMINATOR)
data class ShowNotificationActionConfig(
    val title: String = "Automation started",
    val message: String = "Your rule was triggered."
) : ActionConfig {
    override val type: ActionType = ActionType.SHOW_NOTIFICATION
    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(PostNotificationPermission)

    companion object {
        const val DISCRIMINATOR = "show_notification"
    }
}
