package com.tomtruyen.orkestr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tomtruyen.orkestr.features.automation.navigation.AutomationGraphRoute
import com.tomtruyen.orkestr.features.automation.navigation.AutomationNavGraph

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(AutomationGraphRoute)
    val provider = entryProvider<NavKey> {
        entry<AutomationGraphRoute> {
            AutomationNavGraph()
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = provider,
    )
}
