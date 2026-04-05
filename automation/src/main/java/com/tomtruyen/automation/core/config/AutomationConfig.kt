package com.tomtruyen.automation.core.config

import com.tomtruyen.automation.core.permission.AutomationPermission
import kotlinx.serialization.Transient

interface AutomationConfig<T> {
    val type: T

    val category: AutomationCategory

    @Transient
    val requiredPermissions: List<AutomationPermission>
        get() = emptyList()
}
