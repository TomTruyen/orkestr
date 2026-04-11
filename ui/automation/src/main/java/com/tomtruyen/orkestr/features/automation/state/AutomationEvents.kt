package com.tomtruyen.orkestr.features.automation.state

import com.tomtruyen.automation.core.AutomationRule

sealed interface AutomationRulesEvent {
    data object NavigateToCreateRule : AutomationRulesEvent
    data class NavigateToEditRule(val rule: AutomationRule) : AutomationRulesEvent
}

sealed interface AutomationEditorEvent {
    data object NavigateBackToRules : AutomationEditorEvent
    data class NavigateToDefinitionSelection(val section: RuleSection, val editingIndex: Int?) : AutomationEditorEvent

    data class NavigateToDefinitionConfiguration(
        val section: RuleSection,
        val typeKey: String,
        val editingIndex: Int?,
    ) : AutomationEditorEvent

    data object NavigateToGeofenceConfiguration : AutomationEditorEvent
    data object NavigateToTimeBasedTriggerConfiguration : AutomationEditorEvent
    data object NavigateToGeofenceConstraintConfiguration : AutomationEditorEvent
    data object NavigateToTimeOfDayConstraintConfiguration : AutomationEditorEvent
    data object NavigateToApplicationTriggerAppSelection : AutomationEditorEvent
    data object NavigateToNotificationTriggerAppSelection : AutomationEditorEvent
    data object NavigateToPackageChangedTriggerAppSelection : AutomationEditorEvent
    data object NavigateToLaunchApplicationActionAppSelection : AutomationEditorEvent
    data object NavigateToSetWallpaperActionConfiguration : AutomationEditorEvent
    data object NavigateToWifiTriggerSelection : AutomationEditorEvent
    data object PopToDefinitionSelection : AutomationEditorEvent
    data object PopToEditor : AutomationEditorEvent
}
