package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(VibratePhoneActionConfig.DISCRIMINATOR)
data class VibratePhoneActionConfig(val durationMillis: Int = 500) : ActionConfig {
    override val type: ActionType = ActionType.VIBRATE_PHONE
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "vibrate_phone"
    }
}
