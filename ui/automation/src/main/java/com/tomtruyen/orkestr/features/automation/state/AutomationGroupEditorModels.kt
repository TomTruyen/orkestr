package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

internal fun AutomationNodeGroup.withNodeRemovedAt(index: Int): AutomationNodeGroup = when (type) {
    AutomationNodeGroupType.TRIGGER -> copy(triggers = triggers.toMutableList().also { it.removeAt(index) })
    AutomationNodeGroupType.CONSTRAINT -> copy(constraints = constraints.toMutableList().also { it.removeAt(index) })
    AutomationNodeGroupType.ACTION -> copy(actions = actions.toMutableList().also { it.removeAt(index) })
}

internal fun AutomationNodeGroup.withNodeAdded(config: AutomationConfig<*>): AutomationNodeGroup = when (type) {
    AutomationNodeGroupType.TRIGGER -> copy(triggers = triggers + (config as? TriggerConfig ?: return this))
    AutomationNodeGroupType.CONSTRAINT -> copy(constraints = constraints + (config as? ConstraintConfig ?: return this))
    AutomationNodeGroupType.ACTION -> copy(actions = actions + (config as? ActionConfig ?: return this))
}

internal fun AutomationNodeGroup.nodeCount(): Int = when (type) {
    AutomationNodeGroupType.TRIGGER -> triggers.size
    AutomationNodeGroupType.CONSTRAINT -> constraints.size
    AutomationNodeGroupType.ACTION -> actions.size
}
