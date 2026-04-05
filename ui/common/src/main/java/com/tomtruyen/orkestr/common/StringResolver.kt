package com.tomtruyen.orkestr.common

import android.content.Context
import androidx.annotation.StringRes
import com.tomtruyen.automation.core.definition.AutomationTextResolver

interface StringResolver : AutomationTextResolver {
    fun resolve(@StringRes stringRes: Int, vararg formatArgs: Any): String

    override fun resolve(stringRes: Int, formatArgs: List<Any>): String = resolve(stringRes, *formatArgs.toTypedArray())
}

class AndroidStringResolver(private val context: Context) : StringResolver {
    override fun resolve(stringRes: Int, vararg formatArgs: Any): String = context.getString(stringRes, *formatArgs)
}
