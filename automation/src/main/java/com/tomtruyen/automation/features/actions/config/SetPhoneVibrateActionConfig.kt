package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(SetPhoneVibrateActionConfig.DISCRIMINATOR)
data class SetPhoneVibrateActionConfig(val enabled: Boolean = true) : ActionConfig {
    override val type: ActionType = ActionType.SET_PHONE_VIBRATE
    override val category: AutomationCategory = AutomationCategory.VOLUME
    override val parallelExecutionConflictGroup: ActionExecutionConflictGroup = ActionExecutionConflictGroup.AUDIO_POLICY

    companion object {
        const val DISCRIMINATOR = "set_phone_vibrate"
    }
}
