package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.orkestr.features.automation.state.RuleEditorState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.features.automation.state.RuleValidationState

internal fun RuleEditorState.nodeAt(section: RuleSection, index: Int?): AutomationConfig<*>? = when (section) {
    RuleSection.TRIGGERS -> index?.let(triggers::getOrNull)
    RuleSection.CONSTRAINTS -> index?.let(constraints::getOrNull)
    RuleSection.ACTIONS -> index?.let(actions::getOrNull)
}

internal fun RuleEditorState.requiredPermissionsForNode(section: RuleSection, index: Int): List<AutomationPermission> =
    when (section) {
        RuleSection.TRIGGERS -> triggers.getOrNull(index)?.requiredPermissions.orEmpty()
        RuleSection.CONSTRAINTS -> constraints.getOrNull(index)?.requiredPermissions.orEmpty()
        RuleSection.ACTIONS -> actions.getOrNull(index)?.requiredPermissions.orEmpty()
    }

internal fun RuleEditorState.withTrigger(config: TriggerConfig, index: Int?): RuleEditorState = copy(
    triggers = triggers.replaceAt(index, config),
    validation = RuleValidationState(),
)

internal fun RuleEditorState.withConstraint(config: ConstraintConfig, index: Int?): RuleEditorState = copy(
    constraints = constraints.replaceAt(index, config),
    validation = RuleValidationState(),
)

internal fun RuleEditorState.withAction(config: ActionConfig, index: Int?): RuleEditorState = copy(
    actions = actions.replaceAt(index, config),
    validation = RuleValidationState(),
)

internal fun RuleEditorState.withActionExecutionMode(executionMode: ActionExecutionMode): RuleEditorState = copy(
    actionExecutionMode = executionMode,
    validation = RuleValidationState(),
)

internal fun RuleEditorState.withNodeRemoved(section: RuleSection, index: Int): RuleEditorState = when (section) {
    RuleSection.TRIGGERS -> copy(
        triggers = triggers.toMutableList().also { it.removeAt(index) },
        validation = RuleValidationState(),
    )

    RuleSection.CONSTRAINTS -> copy(
        constraints = constraints.toMutableList().also { it.removeAt(index) },
        validation = RuleValidationState(),
    )

    RuleSection.ACTIONS -> copy(
        actions = actions.toMutableList().also { it.removeAt(index) },
        validation = RuleValidationState(),
    )
}

private fun <T> List<T>.replaceAt(index: Int?, value: T): List<T> {
    val mutable = toMutableList()
    if (index == null) {
        mutable += value
    } else {
        mutable[index] = value
    }
    return mutable
}
