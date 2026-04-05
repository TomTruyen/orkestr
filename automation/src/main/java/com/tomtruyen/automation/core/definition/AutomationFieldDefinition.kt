package com.tomtruyen.automation.core.definition

import androidx.annotation.StringRes
import com.tomtruyen.automation.R
import com.tomtruyen.automation.core.config.AutomationConfig
import kotlin.reflect.KClass

data class AutomationOption(val value: String, @param:StringRes val labelRes: Int)

abstract class AutomationFieldDefinition(
    val id: String,
    @param:StringRes val labelRes: Int,
    val type: AutomationFieldType,
    @param:StringRes val descriptionRes: Int,
    val required: Boolean = true,
    val defaultValue: String = "",
    @param:StringRes val placeholderRes: Int? = null,
    val options: List<AutomationOption> = emptyList(),
) {
    abstract fun readValue(config: AutomationConfig<*>?): String
    abstract fun updateValue(config: AutomationConfig<*>?, input: String): AutomationConfig<*>

    open fun validateInput(input: String, resolver: AutomationTextResolver): List<String> {
        val normalized = input.ifBlank { defaultValue }
        val errors = mutableListOf<String>()

        if (required && normalized.isBlank()) {
            errors += resolver.resolve(
                R.string.automation_definition_error_required,
                listOf(resolver.resolve(labelRes)),
            )
        }

        if (type == AutomationFieldType.NUMBER && normalized.isNotBlank() && normalized.toIntOrNull() == null) {
            errors += resolver.resolve(
                R.string.automation_definition_error_number,
                listOf(resolver.resolve(labelRes)),
            )
        }

        return errors
    }
}

class TypedAutomationFieldDefinition<C : AutomationConfig<*>>(
    private val configClass: KClass<C>,
    private val defaultConfig: C,
    id: String,
    @StringRes labelRes: Int,
    type: AutomationFieldType,
    @StringRes descriptionRes: Int,
    required: Boolean = true,
    defaultValue: String = "",
    @StringRes placeholderRes: Int? = null,
    options: List<AutomationOption> = emptyList(),
    private val reader: (C) -> String,
    private val updater: (C, String) -> C,
    private val inputValidator: (String, AutomationTextResolver) -> List<String> = { _, _ -> emptyList() },
) : AutomationFieldDefinition(
    id = id,
    labelRes = labelRes,
    type = type,
    descriptionRes = descriptionRes,
    required = required,
    defaultValue = defaultValue,
    placeholderRes = placeholderRes,
    options = options,
) {
    override fun readValue(config: AutomationConfig<*>?): String = reader(cast(config) ?: defaultConfig)

    override fun updateValue(config: AutomationConfig<*>?, input: String): AutomationConfig<*> =
        updater(cast(config) ?: defaultConfig, input)

    override fun validateInput(input: String, resolver: AutomationTextResolver): List<String> =
        super.validateInput(input, resolver) + inputValidator(input.ifBlank { defaultValue }, resolver)

    private fun cast(config: AutomationConfig<*>?): C? {
        if (config == null || !configClass.isInstance(config)) return null
        @Suppress("UNCHECKED_CAST")
        return config as C
    }
}
