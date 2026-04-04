package com.tomtruyen.automation.core.definition

import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.triggers.TriggerType

class AutomationDefinitionRegistry(
    val triggers: List<TriggerDefinition<*>>,
    val constraints: List<ConstraintDefinition<*>>,
    val actions: List<ActionDefinition<*>>
) {
    private val triggerMap = triggers.associateBy { it.type }
    private val constraintMap = constraints.associateBy { it.type }
    private val actionMap = actions.associateBy { it.type }

    fun trigger(type: TriggerType): TriggerDefinition<*>? = triggerMap[type]
    fun constraint(type: ConstraintType): ConstraintDefinition<*>? = constraintMap[type]
    fun action(type: ActionType): ActionDefinition<*>? = actionMap[type]
}
