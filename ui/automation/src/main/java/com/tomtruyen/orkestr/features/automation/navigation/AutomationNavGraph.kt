package com.tomtruyen.orkestr.features.automation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.orkestr.common.component.LocalNavigationSharedTransitionScope
import com.tomtruyen.orkestr.common.navigation.premiumBackwardTransition
import com.tomtruyen.orkestr.common.navigation.premiumForwardTransition
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesEvent
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import com.tomtruyen.orkestr.features.geofence.navigation.GeofenceEditorRoute
import com.tomtruyen.orkestr.features.geofence.navigation.GeofenceMapPickerRoute
import com.tomtruyen.orkestr.features.geofence.navigation.GeofenceTriggerConfigurationRoute
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerEvent
import com.tomtruyen.orkestr.features.geofence.viewmodel.GeofenceTriggerViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
@Suppress("CyclomaticComplexMethod")
fun AutomationNavGraph(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    rulesViewModel: AutomationRulesViewModel = koinViewModel(),
    editorViewModel: AutomationRuleEditorViewModel = koinViewModel(),
    geofenceViewModel: GeofenceTriggerViewModel = koinViewModel(),
) {
    val backStack = rememberNavBackStack(AutomationRulesRoute)

    LaunchedEffect(rulesViewModel) {
        rulesViewModel.eventFlow.collectLatest { event ->
            when (event) {
                AutomationRulesEvent.NavigateToCreateRule -> {
                    editorViewModel.setCreateRule()
                    backStack.add(AutomationRuleEditorRoute)
                }

                is AutomationRulesEvent.NavigateToEditRule -> {
                    editorViewModel.setEditRule(event.rule)
                    backStack.add(AutomationRuleEditorRoute)
                }
            }
        }
    }

    LaunchedEffect(editorViewModel) {
        editorViewModel.eventFlow.collectLatest { event ->
            when (event) {
                AutomationEditorEvent.NavigateBackToRules -> {
                    popBackStackUntil<AutomationRulesRoute>(backStack)
                }

                is AutomationEditorEvent.NavigateToDefinitionSelection -> {
                    backStack.add(
                        AutomationDefinitionSelectionRoute(
                            section = event.section,
                            editingIndex = event.editingIndex,
                        ),
                    )
                }

                is AutomationEditorEvent.NavigateToDefinitionConfiguration -> {
                    backStack.add(
                        AutomationDefinitionConfigurationRoute(
                            section = event.section,
                            typeKey = event.typeKey,
                            editingIndex = event.editingIndex,
                        ),
                    )
                }

                AutomationEditorEvent.NavigateToGeofenceConfiguration -> {
                    geofenceViewModel.onAction(
                        GeofenceTriggerAction.LoadConfig(
                            editorViewModel.currentDraftConfigOrDefault(GeofenceTriggerConfig::class),
                        ),
                    )
                    backStack.add(GeofenceTriggerConfigurationRoute)
                }

                AutomationEditorEvent.NavigateToGeofenceConstraintConfiguration -> {
                    geofenceViewModel.onAction(
                        GeofenceTriggerAction.LoadConstraintConfig(
                            editorViewModel.currentDraftConfigOrDefault(GeofenceConstraintConfig::class),
                        ),
                    )
                    backStack.add(GeofenceConstraintConfigurationRoute)
                }

                AutomationEditorEvent.NavigateToTimeBasedTriggerConfiguration -> {
                    backStack.add(TimeBasedTriggerConfigurationRoute)
                }

                AutomationEditorEvent.NavigateToTimeOfDayConstraintConfiguration -> {
                    backStack.add(TimeOfDayConstraintConfigurationRoute)
                }

                AutomationEditorEvent.NavigateToApplicationTriggerAppSelection -> {
                    backStack.add(ApplicationTriggerAppSelectionRoute)
                }

                AutomationEditorEvent.NavigateToNotificationTriggerAppSelection -> {
                    backStack.add(NotificationTriggerAppSelectionRoute)
                }

                AutomationEditorEvent.NavigateToLaunchApplicationActionAppSelection -> {
                    backStack.add(LaunchApplicationActionAppSelectionRoute)
                }

                AutomationEditorEvent.NavigateToSetWallpaperActionConfiguration -> {
                    backStack.add(SetWallpaperActionConfigurationRoute)
                }

                AutomationEditorEvent.NavigateToWifiTriggerSelection -> {
                    backStack.add(WifiTriggerSelectionRoute)
                }

                AutomationEditorEvent.PopToDefinitionSelection -> {
                    popBackStackUntil<AutomationDefinitionSelectionRoute>(backStack)
                }

                AutomationEditorEvent.PopToEditor -> {
                    popBackStackUntil<AutomationRuleEditorRoute>(backStack)
                }
            }
        }
    }

    LaunchedEffect(geofenceViewModel) {
        geofenceViewModel.eventFlow.collectLatest { event ->
            when (event) {
                GeofenceTriggerEvent.NavigateToGeofenceEditor -> {
                    backStack.add(GeofenceEditorRoute)
                }

                GeofenceTriggerEvent.NavigateToMapPicker -> {
                    backStack.add(GeofenceMapPickerRoute)
                }

                GeofenceTriggerEvent.PopBack -> {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                    }
                }

                is GeofenceTriggerEvent.GeofenceSelected -> {
                    editorViewModel.applySelectedGeofence(event.geofence)
                }
            }
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        when (backStack.lastOrNull()) {
            is AutomationDefinitionConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is GeofenceTriggerConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is GeofenceConstraintConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is TimeBasedTriggerConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is TimeOfDayConstraintConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is ApplicationTriggerAppSelectionRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is NotificationTriggerAppSelectionRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is LaunchApplicationActionAppSelectionRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is SetWallpaperActionConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is WifiTriggerSelectionRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
            }

            is GeofenceEditorRoute -> {
                geofenceViewModel.onAction(GeofenceTriggerAction.CloseGeofenceEditorClicked)
            }

            is GeofenceMapPickerRoute -> {
                geofenceViewModel.onAction(GeofenceTriggerAction.CloseMapPickerClicked)
            }

            is AutomationDefinitionSelectionRoute -> {
                editorViewModel.onAction(AutomationEditorAction.ClosePickerClicked)
            }

            is AutomationRuleEditorRoute -> {
                editorViewModel.onAction(AutomationEditorAction.CloseEditorClicked)
            }
        }
    }

    val provider = entryProvider {
        automationEntries(
            rulesViewModel = rulesViewModel,
            editorViewModel = editorViewModel,
            geofenceViewModel = geofenceViewModel,
        )
    }

    SharedTransitionLayout {
        CompositionLocalProvider(LocalNavigationSharedTransitionScope provides this) {
            NavDisplay(
                backStack = backStack,
                modifier = modifier
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
                entryProvider = provider,
                transitionSpec = { premiumForwardTransition() },
                popTransitionSpec = { premiumBackwardTransition() },
                predictivePopTransitionSpec = { premiumBackwardTransition() },
            )
        }
    }
}

private inline fun <reified T : NavKey> popBackStackUntil(backStack: NavBackStack<NavKey>) {
    while (backStack.size > 1 && backStack.lastOrNull() !is T) {
        backStack.removeAt(backStack.lastIndex)
    }
}
