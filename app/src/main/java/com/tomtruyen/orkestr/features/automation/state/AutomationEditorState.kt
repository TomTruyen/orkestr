package com.tomtruyen.orkestr.features.automation.state

import androidx.annotation.StringRes
import com.tomtruyen.automation.data.definition.AutomationFieldDefinition
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.orkestr.R

enum class RuleSection(
    @param:StringRes val titleRes: Int,
    @param:StringRes val helperRes: Int,
    @param:StringRes val singularTitleRes: Int
) {
    TRIGGERS(
        titleRes = R.string.automation_section_triggers_title,
        helperRes = R.string.automation_section_triggers_helper,
        singularTitleRes = R.string.automation_singular_trigger
    ),
    CONSTRAINTS(
        titleRes = R.string.automation_section_constraints_title,
        helperRes = R.string.automation_section_constraints_helper,
        singularTitleRes = R.string.automation_singular_constraint
    ),
    ACTIONS(
        titleRes = R.string.automation_section_actions_title,
        helperRes = R.string.automation_section_actions_helper,
        singularTitleRes = R.string.automation_singular_action
    )
}

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
    val fields: List<AutomationFieldDefinition>
)

data class DefinitionPickerState(
    val section: RuleSection,
    val editingIndex: Int? = null,
    val query: String = "",
    val selectedTypeKey: String? = null,
    val values: Map<String, String> = emptyMap(),
    val errors: List<String> = emptyList()
) {
    val isConfiguring: Boolean
        get() = selectedTypeKey != null
}
