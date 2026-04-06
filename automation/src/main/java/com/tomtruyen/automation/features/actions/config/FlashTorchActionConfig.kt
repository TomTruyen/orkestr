package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.permission.AutomationPermission
import com.tomtruyen.automation.core.permission.CameraPermission
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName(FlashTorchActionConfig.DISCRIMINATOR)
data class FlashTorchActionConfig(
    val pulseCount: Int = 3,
    val onDurationMillis: Int = 250,
    val offDurationMillis: Int = 200,
) : ActionConfig {
    override val type: ActionType = ActionType.FLASH_TORCH
    override val category: AutomationCategory = AutomationCategory.UTILITY

    @Transient
    override val requiredPermissions: List<AutomationPermission> = listOf(CameraPermission)

    companion object {
        const val DISCRIMINATOR = "flash_torch"
    }
}
