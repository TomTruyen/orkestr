package com.tomtruyen.automation.data.definition

import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

enum class AutomationFieldType {
    TEXT,
    NUMBER,
    BOOLEAN,
    SINGLE_CHOICE
}

data class AutomationOption(
    val value: String,
    val label: String
)

data class AutomationFieldDefinition(
    val id: String,
    val label: String,
    val type: AutomationFieldType,
    val description: String,
    val required: Boolean = true,
    val defaultValue: String = "",
    val placeholder: String = "",
    val options: List<AutomationOption> = emptyList()
)

interface AutomationNodeDefinition {
    val key: String
    val title: String
    val description: String
    val fields: List<AutomationFieldDefinition>
    val requiredPermissions: List<AutomationPermission>
        get() = emptyList()
}

interface TriggerDefinition : AutomationNodeDefinition {
    val type: TriggerType
    override val key: String
        get() = type.name

    fun createConfig(values: Map<String, String>): TriggerConfig
    fun valuesOf(config: TriggerConfig): Map<String, String>
    fun summarize(values: Map<String, String>): String
    fun validate(values: Map<String, String>): List<String> = validateFields(fields, values)

    fun normalized(values: Map<String, String>): Map<String, String> = fields.associate { field ->
        field.id to values[field.id].orEmpty().ifBlank { field.defaultValue }
    }
}

interface ConstraintDefinition : AutomationNodeDefinition {
    val type: ConstraintType
    override val key: String
        get() = type.name

    fun createConfig(values: Map<String, String>): ConstraintConfig
    fun valuesOf(config: ConstraintConfig): Map<String, String>
    fun summarize(values: Map<String, String>): String
    fun validate(values: Map<String, String>): List<String> = validateFields(fields, values)

    fun normalized(values: Map<String, String>): Map<String, String> = fields.associate { field ->
        field.id to values[field.id].orEmpty().ifBlank { field.defaultValue }
    }
}

interface ActionDefinition : AutomationNodeDefinition {
    val type: ActionType
    override val key: String
        get() = type.name

    fun createConfig(values: Map<String, String>): ActionConfig
    fun valuesOf(config: ActionConfig): Map<String, String>
    fun summarize(values: Map<String, String>): String
    fun validate(values: Map<String, String>): List<String> = validateFields(fields, values)

    fun normalized(values: Map<String, String>): Map<String, String> = fields.associate { field ->
        field.id to values[field.id].orEmpty().ifBlank { field.defaultValue }
    }
}

class AutomationDefinitionRegistry(
    val triggers: List<TriggerDefinition>,
    val constraints: List<ConstraintDefinition>,
    val actions: List<ActionDefinition>
) {
    private val triggerMap = triggers.associateBy { it.type }
    private val constraintMap = constraints.associateBy { it.type }
    private val actionMap = actions.associateBy { it.type }

    fun trigger(type: TriggerType): TriggerDefinition? = triggerMap[type]
    fun constraint(type: ConstraintType): ConstraintDefinition? = constraintMap[type]
    fun action(type: ActionType): ActionDefinition? = actionMap[type]
}

private fun validateFields(
    fields: List<AutomationFieldDefinition>,
    values: Map<String, String>
): List<String> {
    val errors = mutableListOf<String>()

    fields.forEach { field ->
        val value = values[field.id].orEmpty().ifBlank { field.defaultValue }

        if (field.required && value.isBlank()) {
            errors += "${field.label} is required."
        }

        if (field.type == AutomationFieldType.NUMBER && value.isNotBlank() && value.toIntOrNull() == null) {
            errors += "${field.label} must be a number."
        }
    }

    return errors
}
