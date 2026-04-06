package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(LaunchApplicationActionConfig.DISCRIMINATOR)
data class LaunchApplicationActionConfig(
    val packageName: String = "",
    val appLabel: String = "",
) : ActionConfig {
    override val type: ActionType = ActionType.LAUNCH_APPLICATION
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "launch_application"
    }
}
