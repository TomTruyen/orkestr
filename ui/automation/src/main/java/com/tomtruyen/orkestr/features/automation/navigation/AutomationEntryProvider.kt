package com.tomtruyen.orkestr.features.automation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.orkestr.features.automation.screen.AutomationApplicationTriggerAppSelectionScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationDefinitionConfigurationScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationDefinitionSelectionScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationHomeScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationLaunchApplicationActionAppSelectionScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationNotificationTriggerAppSelectionScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationRuleEditorScreen
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import com.tomtruyen.orkestr.features.geofence.navigation.GeofenceEditorRoute
import com.tomtruyen.orkestr.features.geofence.navigation.GeofenceMapPickerRoute
import com.tomtruyen.orkestr.features.geofence.navigation.GeofenceTriggerConfigurationRoute
import com.tomtruyen.orkestr.features.geofence.screen.AutomationGeofenceConfigurationScreen
import com.tomtruyen.orkestr.features.geofence.screen.AutomationGeofenceEditorScreen
import com.tomtruyen.orkestr.features.geofence.screen.AutomationGeofenceMapPickerScreen
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.viewmodel.GeofenceTriggerViewModel
import com.tomtruyen.orkestr.features.wallpaper.screen.WallpaperActionConfigurationScreen
import com.tomtruyen.orkestr.ui.automation.R
import com.tomtruyen.orkestr.ui.common.R as CommonR
import com.tomtruyen.orkestr.ui.geofence.R as GeofenceR

@Composable
internal fun EntryProviderScope<NavKey>.automationEntries(
    rulesViewModel: AutomationRulesViewModel,
    editorViewModel: AutomationRuleEditorViewModel,
    geofenceViewModel: GeofenceTriggerViewModel,
) {
    entry<AutomationRulesRoute> {
        AutomationScaffold(
            title = stringResource(R.string.automation_title_rules),
            canNavigateBack = false,
            onNavigateBack = null,
        ) { modifier ->
            AutomationHomeScreen(
                viewModel = rulesViewModel,
                summarizeTrigger = editorViewModel::summarizeTrigger,
                summarizeConstraint = editorViewModel::summarizeConstraint,
                summarizeAction = editorViewModel::summarizeAction,
                modifier = modifier,
            )
        }
    }

    entry<AutomationRuleEditorRoute> {
        AutomationScaffold(
            title = stringResource(R.string.automation_title_rule_editor),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.CloseEditorClicked)
            },
        ) { modifier ->
            AutomationRuleEditorScreen(
                viewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<AutomationDefinitionSelectionRoute> { route ->
        AutomationScaffold(
            title = stringResource(
                R.string.automation_title_select_node,
                stringResource(route.section.singularTitleRes),
            ),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.ClosePickerClicked)
            },
        ) { modifier ->
            AutomationDefinitionSelectionScreen(
                viewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<AutomationDefinitionConfigurationRoute> { route ->
        AutomationScaffold(
            title = stringResource(
                R.string.automation_title_configure_node,
                stringResource(route.section.singularTitleRes),
            ),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            AutomationDefinitionConfigurationScreen(
                viewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<GeofenceTriggerConfigurationRoute> {
        AutomationScaffold(
            title = stringResource(
                R.string.automation_title_configure_node,
                stringResource(R.string.automation_singular_trigger),
            ),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            AutomationGeofenceConfigurationScreen(
                viewModel = geofenceViewModel,
                modifier = modifier,
            )
        }
    }

    entry<TimeBasedTriggerConfigurationRoute> {
        AutomationScaffold(
            title = stringResource(
                R.string.automation_title_configure_node,
                stringResource(R.string.automation_singular_trigger),
            ),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            TimeBasedTriggerRouteScreen(
                editorViewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<NotificationTriggerAppSelectionRoute> {
        AutomationScaffold(
            title = stringResource(R.string.automation_title_select_notification_app),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            AutomationNotificationTriggerAppSelectionScreen(
                viewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<ApplicationTriggerAppSelectionRoute> {
        AutomationScaffold(
            title = stringResource(R.string.automation_title_select_notification_app),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            AutomationApplicationTriggerAppSelectionScreen(
                viewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<LaunchApplicationActionAppSelectionRoute> {
        AutomationScaffold(
            title = stringResource(R.string.automation_title_select_app_to_launch),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            AutomationLaunchApplicationActionAppSelectionScreen(
                viewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<SetWallpaperActionConfigurationRoute> {
        val pickerState = editorViewModel.uiState.collectAsState().value.pickerState ?: return@entry
        val definition = editorViewModel.selectedDefinitionItem() ?: return@entry
        AutomationScaffold(
            title = stringResource(
                R.string.automation_title_configure_node,
                stringResource(R.string.automation_singular_action),
            ),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            WallpaperActionConfigurationScreen(
                title = stringResource(definition.titleRes),
                description = stringResource(definition.descriptionRes),
                isBeta = definition.isBeta,
                requiredMinSdk = definition.requiredMinSdk,
                chooseDifferentLabel = stringResource(
                    R.string.automation_action_choose_different,
                    stringResource(pickerState.section.singularTitleRes),
                ),
                config = editorViewModel.currentDraftConfigOrDefault(SetWallpaperActionConfig::class),
                onConfirm = editorViewModel::applySelectedWallpaper,
                onChooseDifferent = editorViewModel::chooseDifferentDefinition,
                modifier = modifier,
            )
        }
    }

    entry<WifiTriggerSelectionRoute> {
        AutomationScaffold(
            title = stringResource(R.string.automation_title_select_wifi_network),
            canNavigateBack = true,
            onNavigateBack = {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            },
        ) { modifier ->
            WifiTriggerRouteScreen(
                editorViewModel = editorViewModel,
                modifier = modifier,
            )
        }
    }

    entry<GeofenceEditorRoute> {
        AutomationScaffold(
            title = stringResource(GeofenceR.string.geofence_title_new_geofence),
            canNavigateBack = true,
            onNavigateBack = {
                geofenceViewModel.onAction(GeofenceTriggerAction.CloseGeofenceEditorClicked)
            },
        ) { modifier ->
            AutomationGeofenceEditorScreen(
                viewModel = geofenceViewModel,
                modifier = modifier,
            )
        }
    }

    entry<GeofenceMapPickerRoute> {
        AutomationScaffold(
            title = stringResource(GeofenceR.string.geofence_map_picker_title),
            canNavigateBack = true,
            onNavigateBack = {
                geofenceViewModel.onAction(GeofenceTriggerAction.CloseMapPickerClicked)
            },
        ) { modifier ->
            AutomationGeofenceMapPickerScreen(
                viewModel = geofenceViewModel,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutomationScaffold(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: (() -> Unit)?,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (canNavigateBack && onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(CommonR.string.automation_action_back),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
