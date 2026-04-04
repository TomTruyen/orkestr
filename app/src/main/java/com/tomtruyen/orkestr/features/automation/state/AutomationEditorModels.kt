package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.data.definition.AutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

data class RuleValidationState(
    val errors: List<String> = emptyList()
)

data class RuleEditorState(
    val id: String,
    val name: String = "",
    val enabled: Boolean = true,
    val triggers: List<TriggerConfig> = emptyList(),
    val constraints: List<ConstraintConfig> = emptyList(),
    val actions: List<ActionConfig> = emptyList(),
    val validation: RuleValidationState = RuleValidationState()
)

data class DefinitionListItem(
    val key: String,
    val title: String,
    val description: String,
    val fields: List<AutomationFieldDefinition>,
    val permissions: List<AutomationPermission> = emptyList()
)

data class DefinitionPickerState(
    val section: RuleSection,
    val editingIndex: Int? = null,
    val query: String = "",
    val launchedFromSelection: Boolean = true,
    val selectedTypeKey: String? = null,
    val values: Map<String, String> = emptyMap(),
    val errors: List<String> = emptyList()
)
