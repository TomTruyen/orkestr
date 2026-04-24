package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.DoNotDisturbMode
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.NotificationPolicyAccessPermission
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(DoNotDisturbActionConfig.DISCRIMINATOR)
data class DoNotDisturbActionConfig(val mode: DoNotDisturbMode = DoNotDisturbMode.PRIORITY_ONLY) : ActionConfig {
    override val type: ActionType = ActionType.DO_NOT_DISTURB
    override val category: AutomationCategory = AutomationCategory.VOLUME
    override val parallelExecutionConflictGroup: ActionExecutionConflictGroup = ActionExecutionConflictGroup.AUDIO_POLICY

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(NotificationPolicyAccessPermission)

    companion object {
        const val DISCRIMINATOR = "do_not_disturb"
    }
}
