package com.tomtruyen.orkestr.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tomtruyen.orkestr.R
import com.tomtruyen.orkestr.common.navigation.premiumBackwardTransition
import com.tomtruyen.orkestr.common.navigation.premiumForwardTransition
import com.tomtruyen.orkestr.features.automation.navigation.AutomationNavGraph
import com.tomtruyen.orkestr.features.automation.screen.AutomationGroupsScreen
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationGroupsViewModel
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.features.logs.screen.AutomationLogsScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    var selectedDestination by rememberSaveable { mutableStateOf(AppDestination.AUTOMATIONS) }
    val stateHolder = rememberSaveableStateHolder()

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = destination == selectedDestination,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.labelRes),
                            )
                        },
                        label = {
                            Text(stringResource(destination.labelRes))
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedDestination,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    premiumForwardTransition()
                } else {
                    premiumBackwardTransition()
                }
            },
            label = "appDestination",
        ) { destination ->
            stateHolder.SaveableStateProvider(destination.name) {
                when (destination) {
                    AppDestination.AUTOMATIONS -> {
                        AutomationNavGraph(
                            modifier = Modifier,
                            contentPadding = innerPadding,
                        )
                    }

                    AppDestination.GROUPS -> {
                        val groupsViewModel = koinViewModel<AutomationGroupsViewModel>()
                        val editorViewModel = koinViewModel<AutomationRuleEditorViewModel>()
                        AutomationGroupsScreen(
                            viewModel = groupsViewModel,
                            summarizeTrigger = editorViewModel::summarizeTrigger,
                            summarizeConstraint = editorViewModel::summarizeConstraint,
                            summarizeAction = editorViewModel::summarizeAction,
                            definitionCategoryGroups = editorViewModel::groupDefinitionCategoryGroups,
                            defaultNodeConfig = editorViewModel::defaultGroupNodeConfig,
                            modifier = Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding),
                        )
                    }

                    AppDestination.LOGS -> {
                        AutomationLogsScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding),
                        )
                    }
                }
            }
        }
    }
}

@Serializable
private enum class AppDestination(val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    AUTOMATIONS(
        labelRes = R.string.app_navigation_automations,
        icon = Icons.Outlined.Tune,
    ),
    GROUPS(
        labelRes = R.string.app_navigation_groups,
        icon = Icons.Outlined.Category,
    ),
    LOGS(
        labelRes = R.string.app_navigation_logs,
        icon = Icons.AutoMirrored.Outlined.ListAlt,
    ),
}
