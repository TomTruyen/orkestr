package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.core.definition.AutomationTextResolver

internal val constraintDefinitionTestResolver = object : AutomationTextResolver {
    override fun resolve(stringRes: Int, formatArgs: List<Any>): String =
        "res:$stringRes" + if (formatArgs.isEmpty()) "" else ":" + formatArgs.joinToString()
}
