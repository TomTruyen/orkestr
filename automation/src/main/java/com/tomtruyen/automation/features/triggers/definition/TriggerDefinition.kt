package com.tomtruyen.automation.features.triggers.definition

import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import kotlin.reflect.KClass

abstract class TriggerDefinition<C : TriggerConfig>(
    final override val configClass: KClass<C>,
    final override val defaultConfig: C
) : AutomationNodeDefinition<C, TriggerType>