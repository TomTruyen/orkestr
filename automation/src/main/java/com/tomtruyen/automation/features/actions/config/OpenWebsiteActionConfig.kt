package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(OpenWebsiteActionConfig.DISCRIMINATOR)
data class OpenWebsiteActionConfig(val url: String = "") : ActionConfig {
    override val type: ActionType = ActionType.OPEN_WEBSITE
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val parallelExecutionConflictGroup: ActionExecutionConflictGroup = ActionExecutionConflictGroup.ACTIVITY_LAUNCH

    companion object {
        const val DISCRIMINATOR = "open_website"
    }
}
