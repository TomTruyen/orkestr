package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.ActionConfig
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.automation.features.constraints.config.ConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TriggerConfig
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState

internal class AutomationRuleEditorCustomFlowCoordinator(
    private val definitions: AutomationDefinitionRegistry,
    private val stringResolver: StringResolver,
    private val state: () -> AutomationEditorUiState,
    private val updateState: ((AutomationEditorUiState) -> AutomationEditorUiState) -> Unit,
    private val triggerEvent: (AutomationEditorEvent) -> Unit,
) {
    fun selectedCustomConfigurationButtonLabel(): String? {
        val picker = state().pickerState ?: return null
        val typeKey = picker.selectedTypeKey ?: return null
        val labelRes = customConfigurationButtonLabelRes(picker.section, typeKey) ?: return null
        return stringResolver.resolve(labelRes)
    }

    fun currentGeofenceTriggerConfig(): GeofenceTriggerConfig = currentDraftConfigOrDefault(GeofenceTriggerConfig())

    fun applySelectedGeofence(config: GeofenceTriggerConfig) {
        val picker = state().pickerState ?: return
        applyDraftConfig(config, picker)
        triggerEvent(
            AutomationEditorEvent.NavigateToDefinitionConfiguration(
                section = picker.section,
                typeKey = config.type.name,
                editingIndex = picker.editingIndex,
            ),
        )
    }

    fun currentNotificationTriggerConfig(): NotificationReceivedTriggerConfig =
        currentDraftConfigOrDefault(NotificationReceivedTriggerConfig())

    fun currentApplicationLifecycleTriggerConfig(): ApplicationLifecycleTriggerConfig =
        currentDraftConfigOrDefault(ApplicationLifecycleTriggerConfig())

    fun currentLaunchApplicationActionConfig(): LaunchApplicationActionConfig =
        currentDraftConfigOrDefault(LaunchApplicationActionConfig())

    fun currentSetWallpaperActionConfig(): SetWallpaperActionConfig =
        currentDraftConfigOrDefault(SetWallpaperActionConfig())

    fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String) {
        when (selectedTypeKey) {
            TriggerType.APPLICATION_LIFECYCLE.name -> {
                val current = currentApplicationLifecycleTriggerConfig()
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }

            ActionType.LAUNCH_APPLICATION.name -> {
                val current = currentLaunchApplicationActionConfig()
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName, appLabel = appLabel))
            }

            else -> {
                val current = currentNotificationTriggerConfig()
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }
        }
    }

    fun currentWifiTriggerConfig(): WifiSsidTriggerConfig = currentDraftConfigOrDefault(WifiSsidTriggerConfig())

    fun applySelectedWifiTrigger(config: WifiSsidTriggerConfig) {
        applyDraftConfigAndOpenConfiguration(config)
    }

    fun applySelectedWallpaper(imageUri: String, imageLabel: String) {
        val current = currentSetWallpaperActionConfig()
        val picker = state().pickerState ?: return
        applyDraftConfig(current.copy(imageUri = imageUri, imageLabel = imageLabel), picker)
        triggerEvent(
            AutomationEditorEvent.NavigateToDefinitionConfiguration(
                section = picker.section,
                typeKey = current.type.name,
                editingIndex = picker.editingIndex,
            ),
        )
    }

    fun openSelectedCustomConfigurationFlow() {
        val picker = state().pickerState ?: return
        val typeKey = picker.selectedTypeKey ?: return
        definitions.customNavigationEventFor(picker.section, typeKey)?.let(triggerEvent)
    }

    fun chooseDifferentDefinition() {
        val picker = state().pickerState ?: return
        backToPickerList(picker)
        if (picker.launchedFromSelection) {
            triggerEvent(AutomationEditorEvent.PopToDefinitionSelection)
        } else {
            triggerEvent(
                AutomationEditorEvent.NavigateToDefinitionSelection(
                    section = picker.section,
                    editingIndex = picker.editingIndex,
                ),
            )
        }
    }

    fun currentTimeBasedTriggerConfig(): TimeBasedTriggerConfig = currentDraftConfigOrDefault(TimeBasedTriggerConfig())

    private fun applyDraftConfigAndOpenConfiguration(config: AutomationConfig<*>) {
        val picker = state().pickerState ?: return
        applyDraftConfig(config, picker)
        triggerEvent(defaultConfigurationEvent(picker, config.type.name))
    }

    private fun applyDraftConfig(config: AutomationConfig<*>, picker: DefinitionPickerState) {
        updateState {
            it.copy(
                pickerState = picker.copy(
                    draftConfig = config,
                    errors = emptyList(),
                ),
            )
        }
    }

    private fun backToPickerList(picker: DefinitionPickerState) {
        updateState {
            it.copy(
                pickerState = picker.copy(
                    selectedTypeKey = null,
                    draftConfig = null,
                    errors = emptyList(),
                ),
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : AutomationConfig<*>> currentDraftConfigOrDefault(defaultConfig: T): T =
        state().pickerState?.draftConfig as? T ?: defaultConfig

    private fun defaultConfigurationEvent(picker: DefinitionPickerState, typeKey: String) =
        AutomationEditorEvent.NavigateToDefinitionConfiguration(
            section = picker.section,
            typeKey = typeKey,
            editingIndex = picker.editingIndex,
        )
}
