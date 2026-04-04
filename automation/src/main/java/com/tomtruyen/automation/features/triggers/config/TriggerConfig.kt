package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import kotlinx.serialization.Serializable

@Serializable
sealed interface TriggerConfig {
    val type: TriggerType
    val requiredPermissions: List<AutomationPermission>
        get() = emptyList()
}
