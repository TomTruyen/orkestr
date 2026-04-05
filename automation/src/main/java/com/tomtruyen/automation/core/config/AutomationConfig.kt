package com.tomtruyen.automation.core.config

import com.tomtruyen.automation.core.permission.AutomationPermission

interface AutomationConfig<T> {
    val type: T

    val category: AutomationCategory

    val requiredPermissions: List<AutomationPermission>
        get() = emptyList()
}
