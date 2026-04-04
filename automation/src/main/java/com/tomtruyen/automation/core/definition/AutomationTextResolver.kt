package com.tomtruyen.automation.core.definition

import androidx.annotation.StringRes

interface AutomationTextResolver {
    fun resolve(@StringRes stringRes: Int, formatArgs: List<Any> = emptyList()): String
}
