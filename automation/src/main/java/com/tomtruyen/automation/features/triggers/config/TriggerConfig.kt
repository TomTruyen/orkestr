package com.tomtruyen.automation.features.triggers.config

import com.tomtruyen.automation.core.AutomationConfig
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.triggers.TriggerType
import kotlinx.serialization.Serializable

@Serializable
sealed interface TriggerConfig: AutomationConfig<TriggerType>
