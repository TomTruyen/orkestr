package com.tomtruyen.orkestr.features.automation.navigation

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.features.automation.screen.AutomationDefinitionConfigurationScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationDefinitionSelectionScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationHomeScreen
import com.tomtruyen.orkestr.features.automation.screen.AutomationRuleEditorScreen
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorAction
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationRulesEvent
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun AutomationNavGraph(
    rulesViewModel: AutomationRulesViewModel = koinViewModel(),
    editorViewModel: AutomationRuleEditorViewModel = koinViewModel(),
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

                AutomationEditorEvent.PopToDefinitionSelection -> {
                    popBackStackUntil<AutomationDefinitionSelectionRoute>(backStack)
                }

                AutomationEditorEvent.PopToEditor -> {
                    popBackStackUntil<AutomationRuleEditorRoute>(backStack)
                }
            }
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        when (backStack.lastOrNull()) {
            is AutomationDefinitionConfigurationRoute -> {
                editorViewModel.onAction(AutomationEditorAction.BackToPickerSelectionClicked)
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
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = provider,
    )
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
                                contentDescription = stringResource(R.string.automation_action_back),
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

private inline fun <reified T : NavKey> popBackStackUntil(backStack: NavBackStack<NavKey>) {
    while (backStack.size > 1 && backStack.lastOrNull() !is T) {
        backStack.removeAt(backStack.lastIndex)
    }
}
