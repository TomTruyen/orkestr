package com.tomtruyen.orkestr.features.wallpaper.screen

import android.content.Intent
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.tomtruyen.automation.core.model.WallpaperTarget
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.ui.wallpaper.R

@Composable
fun WallpaperActionConfigurationScreen(
    title: String,
    description: String,
    isBeta: Boolean,
    requiredMinSdk: Int?,
    chooseDifferentLabel: String?,
    saveLabel: String,
    errors: List<String>,
    config: SetWallpaperActionConfig,
    onFieldChanged: (String, String) -> Unit,
    onSave: () -> Unit,
    onChooseDifferent: (() -> Unit)?,
    onWallpaperSelected: (imageUri: String, imageLabel: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
        val imageLabel = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
        }.orEmpty()

        onWallpaperSelected(
            uri.toString(),
            imageLabel.ifBlank { uri.lastPathSegment.orEmpty() },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(saveLabel)
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AutomationDefinitionHeaderCard(
                    title = title,
                    description = description,
                    isBeta = isBeta,
                    requiredMinSdk = requiredMinSdk,
                    chooseDifferentLabel = chooseDifferentLabel,
                    onChooseDifferent = onChooseDifferent,
                )
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        Text(
                            text = stringResource(R.string.automation_wallpaper_picker_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        OutlinedButton(onClick = { imagePickerLauncher.launch(arrayOf("image/*")) }) {
                            Text(stringResource(R.string.automation_action_choose_image))
                        }
                        if (config.imageUri.isBlank()) {
                            EmptyStateCard(
                                title = stringResource(R.string.automation_empty_wallpaper_title),
                                description = stringResource(R.string.automation_empty_wallpaper_description),
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.automation_wallpaper_selected_image_label),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = config.imageLabel.ifBlank { config.imageUri.toUri().lastPathSegment.orEmpty() },
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        Text(
                            text = stringResource(R.string.automation_wallpaper_apply_to_label),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            WallpaperTarget.entries.forEach { target ->
                                FilterChip(
                                    selected = config.target == target,
                                    onClick = { onFieldChanged("target", target.toFieldValue()) },
                                    label = { Text(text = stringResource(target.toLabelRes())) },
                                )
                            }
                        }
                        if (errors.isNotEmpty()) {
                            ValidationCard(errors = errors)
                        }
                    }
                }
            }
        }
    }
}

private fun WallpaperTarget.toFieldValue(): String = when (this) {
    WallpaperTarget.HOME_SCREEN -> "home"
    WallpaperTarget.LOCK_SCREEN -> "lock"
    WallpaperTarget.HOME_AND_LOCK_SCREEN -> "home_and_lock"
}

private fun WallpaperTarget.toLabelRes(): Int = when (this) {
    WallpaperTarget.HOME_SCREEN -> com.tomtruyen.orkestr.ui.wallpaper.R.string.automation_wallpaper_option_home
    WallpaperTarget.LOCK_SCREEN -> com.tomtruyen.orkestr.ui.wallpaper.R.string.automation_wallpaper_option_lock
    WallpaperTarget.HOME_AND_LOCK_SCREEN -> com.tomtruyen.orkestr.ui.wallpaper.R.string.automation_wallpaper_option_home_and_lock
}
