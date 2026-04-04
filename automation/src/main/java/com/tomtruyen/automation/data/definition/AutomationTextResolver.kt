package com.tomtruyen.automation.data.definition

import androidx.annotation.StringRes

interface AutomationTextResolver {
    fun resolve(@StringRes stringRes: Int, formatArgs: List<Any> = emptyList()): String
}
