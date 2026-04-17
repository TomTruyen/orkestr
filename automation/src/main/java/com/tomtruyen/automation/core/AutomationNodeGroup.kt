package com.tomtruyen.automation.core

import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

data class AutomationNodeGroup(
    val id: String,
    val name: String,
    val type: AutomationNodeGroupType,
    val triggers: List<TriggerConfig> = emptyList(),
    val constraints: List<ConstraintConfig> = emptyList(),
    val actions: List<ActionConfig> = emptyList(),
)

enum class AutomationNodeGroupType {
    TRIGGER,
    CONSTRAINT,
    ACTION,
}
