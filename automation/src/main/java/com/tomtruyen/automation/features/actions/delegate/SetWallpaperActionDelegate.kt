package com.tomtruyen.automation.features.actions.delegate

import android.app.WallpaperManager
import android.content.Context
import androidx.core.net.toUri
import com.tomtruyen.automation.codegen.GenerateActionDelegate
import com.tomtruyen.automation.core.event.AutomationEvent
import com.tomtruyen.automation.core.model.WallpaperTarget
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig

@GenerateActionDelegate
class SetWallpaperActionDelegate(private val context: Context) : ActionDelegate<SetWallpaperActionConfig> {
    override val type: ActionType = ActionType.SET_WALLPAPER

    override suspend fun execute(config: SetWallpaperActionConfig, event: AutomationEvent) {
        if (config.imageUri.isBlank()) return

        val wallpaperManager = WallpaperManager.getInstance(context)
        val uri = config.imageUri.toUri()

        runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val targetFlags = when (config.target) {
                    WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.HOME_AND_LOCK_SCREEN -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wallpaperManager.setStream(
                    input,
                    null,
                    true,
                    targetFlags,
                )
            }
        }
    }
}
