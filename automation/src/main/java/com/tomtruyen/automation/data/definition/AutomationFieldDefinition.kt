package com.tomtruyen.automation.data.definition

import androidx.annotation.StringRes

data class AutomationOption(
    val value: String,
    @param:StringRes val labelRes: Int
)

data class AutomationFieldDefinition(
    val id: String,
    @param:StringRes val labelRes: Int,
    val type: AutomationFieldType,
    @param:StringRes val descriptionRes: Int,
    val required: Boolean = true,
    val defaultValue: String = "",
    @param:StringRes val placeholderRes: Int? = null,
    val options: List<AutomationOption> = emptyList()
)
