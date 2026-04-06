package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.AppLifecycleTransitionType
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.UsageAccessPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.receiver.TriggerReceiverKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(ApplicationLifecycleTriggerConfig.DISCRIMINATOR)
data class ApplicationLifecycleTriggerConfig(
    val packageName: String = "",
    val transitionType: AppLifecycleTransitionType = AppLifecycleTransitionType.LAUNCHED,
) : TriggerConfig {
    override val type: TriggerType = TriggerType.APPLICATION_LIFECYCLE
    override val category: AutomationCategory = AutomationCategory.UTILITY
    override val requiredReceiverKeys: Set<TriggerReceiverKey> = setOf(TriggerReceiverKey.APPLICATION_LIFECYCLE)

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(UsageAccessPermission)

    companion object {
        const val DISCRIMINATOR = "application_lifecycle"
    }
}
