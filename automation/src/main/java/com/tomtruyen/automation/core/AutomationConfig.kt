package com.tomtruyen.automation.core

import com.tomtruyen.automation.core.permission.AutomationPermission

interface AutomationConfig<T> {
    val type: T

    val requiredPermissions: List<AutomationPermission>
        get() = emptyList()
}
