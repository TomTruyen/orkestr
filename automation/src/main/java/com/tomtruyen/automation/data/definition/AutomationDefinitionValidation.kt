package com.tomtruyen.automation.data.definition

import com.tomtruyen.automation.R

internal fun validateFields(
    fields: List<AutomationFieldDefinition>,
    values: Map<String, String>,
    resolver: AutomationTextResolver
): List<String> {
    val errors = mutableListOf<String>()

    fields.forEach { field ->
        val value = values[field.id].orEmpty().ifBlank { field.defaultValue }

        if (field.required && value.isBlank()) {
            errors += resolver.resolve(
                R.string.automation_definition_error_required,
                listOf(resolver.resolve(field.labelRes))
            )
        }

        if (field.type == AutomationFieldType.NUMBER && value.isNotBlank() && value.toIntOrNull() == null) {
            errors += resolver.resolve(
                R.string.automation_definition_error_number,
                listOf(resolver.resolve(field.labelRes))
            )
        }
    }

    return errors
}
