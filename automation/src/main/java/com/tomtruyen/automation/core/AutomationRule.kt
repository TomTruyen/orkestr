package com.tomtruyen.automation.core

import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig

data class AutomationRule(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val triggers: List<TriggerConfig>,
    val constraints: List<ConstraintConfig>,
    val actions: List<ActionConfig>
)