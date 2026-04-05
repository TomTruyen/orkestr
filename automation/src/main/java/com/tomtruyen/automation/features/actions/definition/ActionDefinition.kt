package com.tomtruyen.automation.features.actions.definition

import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import kotlin.reflect.KClass

abstract class ActionDefinition<C : ActionConfig>(
    final override val configClass: KClass<C>,
    final override val defaultConfig: C,
) : AutomationNodeDefinition<C, ActionType>
