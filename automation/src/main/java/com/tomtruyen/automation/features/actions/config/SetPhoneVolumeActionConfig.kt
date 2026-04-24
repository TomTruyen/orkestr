package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.PhoneVolumeStream
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(SetPhoneVolumeActionConfig.DISCRIMINATOR)
data class SetPhoneVolumeActionConfig(
    val stream: PhoneVolumeStream = PhoneVolumeStream.MEDIA,
    val levelPercent: Int = 50,
) : ActionConfig {
    override val type: ActionType = ActionType.SET_PHONE_VOLUME
    override val category: AutomationCategory = AutomationCategory.VOLUME
    override val parallelExecutionConflictGroup: ActionExecutionConflictGroup = ActionExecutionConflictGroup.AUDIO_POLICY

    companion object {
        const val DISCRIMINATOR = "set_phone_volume"
    }
}
