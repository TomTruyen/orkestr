package com.tomtruyen.automation.features.constraints.config

import com.tomtruyen.automation.core.AutomationConfig
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.features.constraints.ConstraintType
import kotlinx.serialization.Serializable

@Serializable
sealed interface ConstraintConfig: AutomationConfig<ConstraintType>
