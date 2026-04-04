package com.tomtruyen.orkestr.common

import android.content.Context
import androidx.annotation.StringRes

fun interface StringResolver {
    fun resolve(@StringRes stringRes: Int): String
}

class AndroidStringResolver(
    private val context: Context
) : StringResolver {
    override fun resolve(stringRes: Int): String = context.getString(stringRes)
}
