package com.tomtruyen.orkestr.features.wallpaper.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tomtruyen.automation.core.model.WallpaperTarget
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun WallpaperActionConfigurationComposePreview() {
    WallpaperActionConfigurationScreen(
        title = "Set Wallpaper",
        description = "Pick an image from the gallery or another document provider and apply it to the selected wallpaper target.",
        isBeta = false,
        requiredMinSdk = 26,
        chooseDifferentLabel = "Choose Different Action",
        saveLabel = "Add Action",
        errors = emptyList(),
        config = SetWallpaperActionConfig(
            imageUri = "content://media/external/images/media/42",
            imageLabel = "Mountains.jpg",
            target = WallpaperTarget.HOME_AND_LOCK_SCREEN,
        ),
        onFieldChanged = { _, _ -> },
        onSave = {},
        onChooseDifferent = {},
        onWallpaperSelected = { _, _ -> },
    )
}
