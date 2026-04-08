package com.tomtruyen.orkestr.features.automation.navigation

import androidx.navigation3.runtime.NavKey
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import kotlinx.serialization.Serializable

@Serializable
data object AutomationGraphRoute : NavKey

@Serializable
data object AutomationRulesRoute : NavKey

@Serializable
data object AutomationRuleEditorRoute : NavKey

@Serializable
data class AutomationDefinitionSelectionRoute(val section: RuleSection, val editingIndex: Int? = null) : NavKey

@Serializable
data class AutomationDefinitionConfigurationRoute(
    val section: RuleSection,
    val typeKey: String,
    val editingIndex: Int? = null,
) : NavKey

@Serializable
data object NotificationTriggerAppSelectionRoute : NavKey

@Serializable
data object ApplicationTriggerAppSelectionRoute : NavKey

@Serializable
data object LaunchApplicationActionAppSelectionRoute : NavKey

@Serializable
data object SetWallpaperActionConfigurationRoute : NavKey

@Serializable
data object TimeBasedTriggerConfigurationRoute : NavKey

@Serializable
data object TimeOfDayConstraintConfigurationRoute : NavKey

@Serializable
data object WifiTriggerSelectionRoute : NavKey

@Serializable
data object GeofenceConstraintConfigurationRoute : NavKey
