package com.tomtruyen.orkestr.features.automation.navigation

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
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
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRulesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AutomationNavGraph(
    rulesViewModel: AutomationRulesViewModel = koinViewModel(),
    editorViewModel: AutomationRuleEditorViewModel = koinViewModel()
) {
    val rules by rulesViewModel.rules.collectAsState()
    val editorState = editorViewModel.editorState
    val pickerState = editorViewModel.pickerState
    val backStack = rememberNavBackStack(AutomationRulesRoute)

    BackHandler(enabled = backStack.size > 1) {
        handleBack(backStack, editorViewModel)
    }

    val provider = entryProvider {
        entry<AutomationRulesRoute> {
            AutomationScaffold(
                title = stringResource(R.string.automation_title_rules),
                canNavigateBack = false,
                onNavigateBack = null
            ) { modifier ->
                AutomationHomeScreen(
                    rules = rules,
                    onCreateRule = {
                        editorViewModel.createRule()
                        backStack.add(AutomationRuleEditorRoute)
                    },
                    onEditRule = { rule ->
                        editorViewModel.editRule(rule)
                        backStack.add(AutomationRuleEditorRoute)
                    },
                    onDeleteRule = rulesViewModel::deleteRule,
                    onToggleRuleEnabled = rulesViewModel::toggleRuleEnabled,
                    summarizeTrigger = editorViewModel::summarizeTrigger,
                    summarizeConstraint = editorViewModel::summarizeConstraint,
                    summarizeAction = editorViewModel::summarizeAction,
                    modifier = modifier
                )
            }
        }

        entry<AutomationRuleEditorRoute> {
            val state = editorState ?: return@entry
            AutomationScaffold(
                title = stringResource(R.string.automation_title_rule_editor),
                canNavigateBack = true,
                onNavigateBack = {
                    editorViewModel.closeEditor()
                    popBackStack(backStack)
                }
            ) { modifier ->
                AutomationRuleEditorScreen(
                    state = state,
                    onRuleNameChanged = editorViewModel::updateRuleName,
                    onRuleEnabledChanged = editorViewModel::updateRuleEnabled,
                    onSaveRule = {
                        editorViewModel.saveRule {
                            popToRules(backStack)
                        }
                    },
                    onOpenPicker = { section, index ->
                        editorViewModel.startSelection(section, index)
                        val selectedTypeKey = editorViewModel.pickerState?.selectedTypeKey
                        if (index != null && selectedTypeKey != null) {
                            backStack.add(
                                AutomationDefinitionConfigurationRoute(
                                    section = section,
                                    typeKey = selectedTypeKey,
                                    editingIndex = index
                                )
                            )
                        } else {
                            backStack.add(AutomationDefinitionSelectionRoute(section, index))
                        }
                    },
                    onDeleteNode = editorViewModel::deleteNode,
                    summarizeTrigger = editorViewModel::summarizeTrigger,
                    summarizeConstraint = editorViewModel::summarizeConstraint,
                    summarizeAction = editorViewModel::summarizeAction,
                    modifier = modifier
                )
            }
        }

        entry<AutomationDefinitionSelectionRoute> { route ->
            val state = pickerState ?: return@entry
            AutomationScaffold(
                title = stringResource(
                    R.string.automation_title_select_node,
                    stringResource(route.section.singularTitleRes)
                ),
                canNavigateBack = true,
                onNavigateBack = {
                    editorViewModel.closePicker()
                    popBackStack(backStack)
                }
            ) { modifier ->
                AutomationDefinitionSelectionScreen(
                    state = state,
                    items = editorViewModel.definitionItems(route.section, state.query),
                    onQueryChanged = editorViewModel::updatePickerQuery,
                    onSelectDefinition = { typeKey ->
                        editorViewModel.openConfiguration(typeKey)
                        backStack.add(
                            AutomationDefinitionConfigurationRoute(
                                section = route.section,
                                typeKey = typeKey,
                                editingIndex = route.editingIndex
                            )
                        )
                    },
                    modifier = modifier
                )
            }
        }

        entry<AutomationDefinitionConfigurationRoute> { route ->
            val state = pickerState ?: return@entry
            val definition = editorViewModel.definitionItems(route.section, "")
                .firstOrNull { it.key == route.typeKey }
                ?: return@entry

            AutomationScaffold(
                title = stringResource(
                    R.string.automation_title_configure_node,
                    stringResource(route.section.singularTitleRes)
                ),
                canNavigateBack = true,
                onNavigateBack = {
                    editorViewModel.backToPickerList()
                    popBackStack(backStack)
                }
            ) { modifier ->
                AutomationDefinitionConfigurationScreen(
                    state = state,
                    definition = definition,
                    onBackToList = {
                        editorViewModel.backToPickerList()
                        popBackStack(backStack)
                    },
                    onFieldChanged = editorViewModel::updatePickerField,
                    onSave = {
                        editorViewModel.savePickerSelection()
                        popBackStack(backStack)
                        popBackStack(backStack)
                    },
                    modifier = modifier
                )
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = provider
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutomationScaffold(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: (() -> Unit)?,
    content: @Composable (Modifier) -> Unit
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
                                contentDescription = stringResource(R.string.automation_action_back)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}

private fun handleBack(
    backStack: NavBackStack<NavKey>,
    editorViewModel: AutomationRuleEditorViewModel
) {
    when (backStack.lastOrNull()) {
        is AutomationDefinitionConfigurationRoute -> {
            editorViewModel.backToPickerList()
            popBackStack(backStack)
        }

        is AutomationDefinitionSelectionRoute -> {
            editorViewModel.closePicker()
            popBackStack(backStack)
        }

        is AutomationRuleEditorRoute -> {
            editorViewModel.closeEditor()
            popBackStack(backStack)
        }
    }
}

private fun popBackStack(backStack: NavBackStack<NavKey>) {
    if (backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }
}

private fun popToRules(backStack: NavBackStack<NavKey>) {
    while (backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }
}
