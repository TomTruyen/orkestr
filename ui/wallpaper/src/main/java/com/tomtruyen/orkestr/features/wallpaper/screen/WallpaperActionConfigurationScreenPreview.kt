package com.tomtruyen.orkestr.features.wallpaper.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig

@Preview(showBackground = true, widthDp = 420, heightDp = 940)
@Composable
internal fun WallpaperActionConfigurationComposePreview() {
    WallpaperActionConfigurationScreen(
        title = "Set Wallpaper",
        description = (
            "Pick an image from the gallery or another document provider, preview it, " +
                "and confirm before returning to the action settings."
            ),
        isBeta = false,
        requiredMinSdk = 26,
        chooseDifferentLabel = "Choose Different Action",
        config = SetWallpaperActionConfig(
            imageUri = "content://media/external/images/media/42",
            imageLabel = "Mountains.jpg",
        ),
        onConfirm = { _, _ -> },
        onChooseDifferent = {},
    )
}
