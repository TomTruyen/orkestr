package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.core.definition.AutomationNodeDefinition
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.DefinitionCategoryGroup
import com.tomtruyen.orkestr.features.automation.state.DefinitionListItem
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import com.tomtruyen.orkestr.ui.automation.R

internal fun AutomationDefinitionRegistry.definitionItems(
    section: RuleSection,
    query: String,
    stringResolver: StringResolver,
): List<DefinitionListItem> = definitionsFor(section)
    .filter { definition ->
        query.isBlank() ||
            stringResolver.resolve(definition.titleRes).contains(query, ignoreCase = true) ||
            stringResolver.resolve(definition.descriptionRes).contains(query, ignoreCase = true)
    }
    .map(::toDefinitionListItem)

internal fun AutomationDefinitionRegistry.definitionCategoryGroups(
    section: RuleSection,
    query: String,
    stringResolver: StringResolver,
): List<DefinitionCategoryGroup> = definitionItems(section, query, stringResolver)
    .groupBy { it.category }
    .toList()
    .sortedBy { (category, _) -> stringResolver.resolve(category.titleRes) }
    .map { (category, items) ->
        DefinitionCategoryGroup(
            category = category,
            items = items.sortedBy { item -> stringResolver.resolve(item.titleRes) },
        )
    }

internal fun AutomationDefinitionRegistry.selectedDefinitionItem(
    pickerState: DefinitionPickerState?,
): DefinitionListItem? {
    val picker = pickerState ?: return null
    val typeKey = picker.selectedTypeKey ?: return null
    val definition = definitionFor(picker.section, typeKey) ?: return null
    return toDefinitionListItem(definition)
}

internal fun AutomationDefinitionRegistry.defaultConfig(section: RuleSection, typeKey: String): AutomationConfig<*>? =
    definitionFor(section, typeKey)?.initialConfig()

internal fun AutomationDefinitionRegistry.definitionFor(
    section: RuleSection,
    typeKey: String,
): AutomationNodeDefinition<*, *>? = when (section) {
    RuleSection.TRIGGERS -> trigger(TriggerType.valueOf(typeKey))
    RuleSection.CONSTRAINTS -> constraint(ConstraintType.valueOf(typeKey))
    RuleSection.ACTIONS -> action(ActionType.valueOf(typeKey))
}

internal fun AutomationDefinitionRegistry.customNavigationEventFor(
    section: RuleSection,
    typeKey: String,
): AutomationEditorEvent? {
    return when (section) {
        RuleSection.TRIGGERS -> when (typeKey) {
            TriggerType.GEOFENCE.name -> AutomationEditorEvent.NavigateToGeofenceConfiguration
            TriggerType.TIME_BASED.name -> AutomationEditorEvent.NavigateToTimeBasedTriggerConfiguration
            TriggerType.APPLICATION_LIFECYCLE.name -> AutomationEditorEvent.NavigateToApplicationTriggerAppSelection
            TriggerType.NOTIFICATION_RECEIVED.name -> AutomationEditorEvent.NavigateToNotificationTriggerAppSelection
            TriggerType.WIFI_SSID_IN_RANGE.name -> AutomationEditorEvent.NavigateToWifiTriggerSelection
            else -> null
        }

        RuleSection.ACTIONS -> when (typeKey) {
            ActionType.LAUNCH_APPLICATION.name -> AutomationEditorEvent.NavigateToLaunchApplicationActionAppSelection
            ActionType.SET_WALLPAPER.name -> AutomationEditorEvent.NavigateToSetWallpaperActionConfiguration
            else -> null
        }

        RuleSection.CONSTRAINTS -> null
    }
}

internal fun customConfigurationButtonLabelRes(section: RuleSection, typeKey: String): Int? = when (section) {
    RuleSection.TRIGGERS -> when (typeKey) {
        TriggerType.GEOFENCE.name -> R.string.automation_action_open_geofence_flow
        TriggerType.TIME_BASED.name -> R.string.automation_action_open_schedule_flow
        TriggerType.APPLICATION_LIFECYCLE.name -> R.string.automation_action_open_app_picker_flow
        TriggerType.NOTIFICATION_RECEIVED.name -> R.string.automation_action_open_app_picker_flow
        TriggerType.WIFI_SSID_IN_RANGE.name -> R.string.automation_action_open_wifi_picker_flow
        else -> null
    }

    RuleSection.ACTIONS -> when (typeKey) {
        ActionType.LAUNCH_APPLICATION.name -> R.string.automation_action_open_app_picker_flow
        ActionType.SET_WALLPAPER.name -> R.string.automation_action_open_wallpaper_picker_flow
        else -> null
    }

    RuleSection.CONSTRAINTS -> null
}

private fun AutomationDefinitionRegistry.definitionsFor(section: RuleSection): List<AutomationNodeDefinition<*, *>> =
    when (section) {
        RuleSection.TRIGGERS -> triggers
        RuleSection.CONSTRAINTS -> constraints
        RuleSection.ACTIONS -> actions
    }

private fun toDefinitionListItem(definition: AutomationNodeDefinition<*, *>) = DefinitionListItem(
    key = definition.key,
    titleRes = definition.titleRes,
    descriptionRes = definition.descriptionRes,
    category = definition.category,
    fields = definition.fields,
    permissions = definition.requiredPermissions,
    requiredMinSdk = definition.requiredMinSdk,
    isBeta = definition.isBeta,
)
