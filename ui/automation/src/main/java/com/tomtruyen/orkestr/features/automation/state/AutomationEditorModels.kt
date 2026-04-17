package com.tomtruyen.orkestr.features.automation.state

import androidx.annotation.StringRes
import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationFieldDefinition
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

data class RuleValidationState(val errors: List<String> = emptyList())

data class RuleEditorState(
    val id: String,
    val name: String = "",
    val enabled: Boolean = true,
    val triggers: List<TriggerConfig> = emptyList(),
    val constraints: List<ConstraintConfig> = emptyList(),
    val actions: List<ActionConfig> = emptyList(),
    val actionExecutionMode: ActionExecutionMode = ActionExecutionMode.PARALLEL,
    val validation: RuleValidationState = RuleValidationState(),
)

data class DefinitionListItem(
    val key: String,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    val category: AutomationCategory,
    val fields: List<AutomationFieldDefinition>,
    val permissions: List<AutomationPermission> = emptyList(),
    val requiredMinSdk: Int? = null,
    val isBeta: Boolean = false,
)

data class DefinitionCategoryGroup(val category: AutomationCategory, val items: List<DefinitionListItem>)

data class DefinitionPickerState(
    val section: RuleSection,
    val editingIndex: Int? = null,
    val query: String = "",
    val launchedFromSelection: Boolean = true,
    val selectedTypeKey: String? = null,
    val draftConfig: AutomationConfig<*>? = null,
    val errors: List<String> = emptyList(),
)

data class DefinitionGroupListItem(val group: AutomationNodeGroup, val summaries: List<String>)
