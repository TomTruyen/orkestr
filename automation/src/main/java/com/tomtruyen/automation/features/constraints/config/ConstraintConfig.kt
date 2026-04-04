package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.Serializable

@Serializable
sealed interface ConstraintConfig {
    val type: ConstraintType
    val requiredPermissions: List<AutomationPermission>
        get() = emptyList()
}
