package com.tomtruyen.orkestr.features.wallpaper.screen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.OpenableColumns
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.ui.wallpaper.R

@Composable
fun WallpaperActionConfigurationScreen(
    title: String,
    description: String,
    isBeta: Boolean,
    requiredMinSdk: Int?,
    chooseDifferentLabel: String?,
    config: SetWallpaperActionConfig,
    onConfirm: (imageUri: String, imageLabel: String) -> Unit,
    onChooseDifferent: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val inspectionMode = LocalInspectionMode.current
    val deviceAspectRatio = if (configuration.screenWidthDp > 0) {
        configuration.screenHeightDp.toFloat() / configuration.screenWidthDp.toFloat()
    } else {
        16f / 9f
    }
    var selectedImageUri by rememberSaveable(config.imageUri) { mutableStateOf(config.imageUri) }
    var selectedImageLabel by rememberSaveable(config.imageLabel) { mutableStateOf(config.imageLabel) }
    val previewBitmap by produceState<Bitmap?>(initialValue = null, selectedImageUri, inspectionMode) {
        value = if (inspectionMode || selectedImageUri.isBlank()) {
            null
        } else {
            runCatching {
                val uri = selectedImageUri.toUri()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri),
                    )
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            }.getOrNull()
        }
    }
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

        selectedImageUri = uri.toString()
        selectedImageLabel = imageLabel.ifBlank { uri.lastPathSegment.orEmpty() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card {
                Button(
                    onClick = {
                        if (selectedImageUri.isNotBlank()) {
                            onConfirm(selectedImageUri, selectedImageLabel)
                        }
                    },
                    enabled = selectedImageUri.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(stringResource(R.string.automation_action_confirm_wallpaper))
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
                        if (selectedImageUri.isBlank()) {
                            EmptyStateCard(
                                title = stringResource(R.string.automation_empty_wallpaper_title),
                                description = stringResource(R.string.automation_empty_wallpaper_description),
                            )
                        } else {
                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                val previewHeight = maxWidth * deviceAspectRatio
                                if (inspectionMode || previewBitmap == null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(previewHeight)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                    ) {
                                        Text(
                                            text = selectedImageLabel.ifBlank {
                                                selectedImageUri.toUri().lastPathSegment.orEmpty()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                } else {
                                    Image(
                                        bitmap = previewBitmap!!.asImageBitmap(),
                                        contentDescription = selectedImageLabel,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(previewHeight)
                                            .clip(RoundedCornerShape(20.dp)),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.automation_wallpaper_selected_image_label),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = selectedImageLabel.ifBlank {
                                    selectedImageUri.toUri().lastPathSegment.orEmpty()
                                },
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}
