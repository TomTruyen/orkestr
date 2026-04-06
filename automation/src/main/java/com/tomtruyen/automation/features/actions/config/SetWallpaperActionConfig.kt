package com.tomtruyen.automation.features.actions.config

import com.tomtruyen.automation.core.config.AutomationCategory
import com.tomtruyen.automation.core.model.WallpaperTarget
import com.tomtruyen.automation.features.actions.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(SetWallpaperActionConfig.DISCRIMINATOR)
data class SetWallpaperActionConfig(
    val imageUri: String = "",
    val imageLabel: String = "",
    val target: WallpaperTarget = WallpaperTarget.HOME_AND_LOCK_SCREEN,
) : ActionConfig {
    override val type: ActionType = ActionType.SET_WALLPAPER
    override val category: AutomationCategory = AutomationCategory.UTILITY

    companion object {
        const val DISCRIMINATOR = "set_wallpaper"
    }
}
