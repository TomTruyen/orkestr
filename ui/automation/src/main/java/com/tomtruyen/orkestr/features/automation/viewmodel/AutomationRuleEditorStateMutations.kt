package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.ConstraintGroup
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.effectiveConstraintGroups
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
    constraintGroups = constraintGroups.withFlatConstraintReplaced(constraints, index, config),
    validation = RuleValidationState(),
)

internal fun RuleEditorState.withConstraintInConditionGroup(
    config: ConstraintConfig,
    groupIndex: Int,
): RuleEditorState {
    if (groupIndex !in constraintGroups.indices) return withConstraint(config, index = null)

    return copy(
        constraints = constraints + config,
        constraintGroups = constraintGroups.mapIndexed { index, group ->
            if (index == groupIndex) {
                group.copy(constraints = group.constraints + config)
            } else {
                group
            }
        },
        validation = RuleValidationState(),
    )
}

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
        constraintGroups = constraintGroups.withFlatConstraintRemoved(constraints, index),
        validation = RuleValidationState(),
    )

    RuleSection.ACTIONS -> copy(
        actions = actions.toMutableList().also { it.removeAt(index) },
        validation = RuleValidationState(),
    )
}

internal fun RuleEditorState.withConstraintConditionGroup(indices: Set<Int>): RuleEditorState {
    val selectedIndices = indices.filter { it in constraints.indices }.toSet()
    if (selectedIndices.isEmpty()) return this

    val selected = constraints.filterIndexed { index, _ -> index in selectedIndices }
    val unselectedGroups = constraints
        .filterIndexed { index, _ -> index !in selectedIndices }
        .map { ConstraintGroup(listOf(it)) }

    return copy(
        constraintGroups = listOf(ConstraintGroup(selected)) + unselectedGroups,
        validation = RuleValidationState(),
    )
}

internal fun RuleEditorState.withConstraintConditionGroupUpdated(groupIndex: Int, indices: Set<Int>): RuleEditorState {
    if (groupIndex !in constraintGroups.indices) return this
    val selectedIndices = indices.filter { it in constraints.indices }.toSet()
    if (selectedIndices.isEmpty()) return withConstraintConditionGroupDeleted(groupIndex)

    val selected = constraints.filterIndexed { index, _ -> index in selectedIndices }
    return copy(
        constraintGroups = constraintGroups.mapIndexed { index, group ->
            if (index == groupIndex) {
                group.copy(constraints = selected)
            } else {
                group
            }
        },
        validation = RuleValidationState(),
    )
}

internal fun RuleEditorState.withConstraintConditionGroupDeleted(groupIndex: Int): RuleEditorState {
    if (groupIndex !in constraintGroups.indices) return this
    return copy(
        constraintGroups = constraintGroups.toMutableList().also { it.removeAt(groupIndex) },
        validation = RuleValidationState(),
    )
}

internal fun RuleEditorState.withConstraintRemovedFromConditionGroup(
    groupIndex: Int,
    constraintIndex: Int,
): RuleEditorState {
    if (groupIndex !in constraintGroups.indices || constraintIndex !in constraints.indices) return this
    val constraint = constraints[constraintIndex]
    val updatedGroup = constraintGroups[groupIndex].copy(
        constraints = constraintGroups[groupIndex].constraints.toMutableList().also {
            it.remove(constraint)
        },
    )

    return copy(
        constraintGroups = constraintGroups.toMutableList().also { groups ->
            if (updatedGroup.constraints.isEmpty()) {
                groups.removeAt(groupIndex)
            } else {
                groups[groupIndex] = updatedGroup
            }
        },
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

private fun List<ConstraintGroup>.withFlatConstraintReplaced(
    fallbackConstraints: List<ConstraintConfig>,
    index: Int?,
    value: ConstraintConfig,
): List<ConstraintGroup> {
    if (index == null) {
        return if (isEmpty()) {
            emptyList()
        } else {
            effectiveConstraintGroups(
                fallbackConstraints,
            ) + ConstraintGroup(listOf(value))
        }
    }
    var currentIndex = 0
    return effectiveConstraintGroups(fallbackConstraints).map { group ->
        group.copy(
            constraints = group.constraints.map { constraint ->
                if (currentIndex++ == index) value else constraint
            },
        )
    }
}

private fun List<ConstraintGroup>.withFlatConstraintRemoved(
    fallbackConstraints: List<ConstraintConfig>,
    index: Int,
): List<ConstraintGroup> {
    var currentIndex = 0
    return effectiveConstraintGroups(fallbackConstraints)
        .map { group ->
            group.copy(
                constraints = group.constraints.filter {
                    currentIndex++ != index
                },
            )
        }
        .filter { it.constraints.isNotEmpty() }
}
