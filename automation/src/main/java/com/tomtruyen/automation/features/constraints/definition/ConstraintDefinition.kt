package com.tomtruyen.automation.features.constraints.definition

import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import kotlin.reflect.KClass

abstract class ConstraintDefinition<C : ConstraintConfig>(
    final override val configClass: KClass<C>,
    final override val defaultConfig: C,
) : AutomationNodeDefinition<C, ConstraintType>
