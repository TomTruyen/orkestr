package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import kotlin.reflect.KClass

internal interface AutomationRuleEditorCustomFlowDelegate {
    fun selectedCustomConfigurationButtonLabel(): String?
    fun <T : AutomationConfig<*>> currentDraftConfigOrDefault(configClass: KClass<T>): T
    fun applySelectedGeofence(config: GeofenceTriggerConfig)
    fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String)
    fun applySelectedWifiTrigger(config: WifiSsidTriggerConfig)
    fun applySelectedWallpaper(imageUri: String, imageLabel: String)
    fun openSelectedCustomConfigurationFlow()
    fun chooseDifferentDefinition()
}

internal class BindableAutomationRuleEditorCustomFlowDelegate : AutomationRuleEditorCustomFlowDelegate {
    private var delegate: AutomationRuleEditorCustomFlowDelegate? = null

    fun bind(
        definitions: AutomationDefinitionRegistry,
        stringResolver: StringResolver,
        state: () -> AutomationEditorUiState,
        updateState: ((AutomationEditorUiState) -> AutomationEditorUiState) -> Unit,
        triggerEvent: (AutomationEditorEvent) -> Unit,
    ) {
        delegate = AutomationRuleEditorCustomFlowCoordinator(
            definitions = definitions,
            stringResolver = stringResolver,
            state = state,
            updateState = updateState,
            triggerEvent = triggerEvent,
        )
    }

    override fun selectedCustomConfigurationButtonLabel(): String? =
        requireDelegate().selectedCustomConfigurationButtonLabel()

    override fun <T : AutomationConfig<*>> currentDraftConfigOrDefault(configClass: KClass<T>): T =
        requireDelegate().currentDraftConfigOrDefault(configClass)

    override fun applySelectedGeofence(config: GeofenceTriggerConfig) = requireDelegate().applySelectedGeofence(config)

    override fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String) =
        requireDelegate().applySelectedApp(selectedTypeKey, packageName, appLabel)

    override fun applySelectedWifiTrigger(config: WifiSsidTriggerConfig) =
        requireDelegate().applySelectedWifiTrigger(config)

    override fun applySelectedWallpaper(imageUri: String, imageLabel: String) =
        requireDelegate().applySelectedWallpaper(imageUri, imageLabel)

    override fun openSelectedCustomConfigurationFlow() = requireDelegate().openSelectedCustomConfigurationFlow()

    override fun chooseDifferentDefinition() = requireDelegate().chooseDifferentDefinition()

    private fun requireDelegate(): AutomationRuleEditorCustomFlowDelegate =
        delegate ?: error("AutomationRuleEditorCustomFlowDelegate has not been bound")
}

internal class AutomationRuleEditorCustomFlowCoordinator(
    private val definitions: AutomationDefinitionRegistry,
    private val stringResolver: StringResolver,
    private val state: () -> AutomationEditorUiState,
    private val updateState: ((AutomationEditorUiState) -> AutomationEditorUiState) -> Unit,
    private val triggerEvent: (AutomationEditorEvent) -> Unit,
) : AutomationRuleEditorCustomFlowDelegate {
    override fun selectedCustomConfigurationButtonLabel(): String? {
        val picker = state().pickerState ?: return null
        val typeKey = picker.selectedTypeKey ?: return null
        val labelRes = customConfigurationButtonLabelRes(picker.section, typeKey) ?: return null
        return stringResolver.resolve(labelRes)
    }

    override fun <T : AutomationConfig<*>> currentDraftConfigOrDefault(configClass: KClass<T>): T {
        val draftConfig = state().pickerState?.draftConfig
        if (configClass.isInstance(draftConfig)) {
            @Suppress("UNCHECKED_CAST")
            return draftConfig as T
        }

        val defaultConfig = when (configClass) {
            GeofenceTriggerConfig::class -> GeofenceTriggerConfig()
            NotificationReceivedTriggerConfig::class -> NotificationReceivedTriggerConfig()
            ApplicationLifecycleTriggerConfig::class -> ApplicationLifecycleTriggerConfig()
            LaunchApplicationActionConfig::class -> LaunchApplicationActionConfig()
            SetWallpaperActionConfig::class -> SetWallpaperActionConfig()
            WifiSsidTriggerConfig::class -> WifiSsidTriggerConfig()
            TimeBasedTriggerConfig::class -> TimeBasedTriggerConfig()
            else -> error("Unsupported config class: ${configClass.qualifiedName}")
        }

        @Suppress("UNCHECKED_CAST")
        return defaultConfig as T
    }

    override fun applySelectedGeofence(config: GeofenceTriggerConfig) {
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

    override fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String) {
        when (selectedTypeKey) {
            TriggerType.APPLICATION_LIFECYCLE.name -> {
                val current = currentDraftConfigOrDefault(ApplicationLifecycleTriggerConfig::class)
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }

            ActionType.LAUNCH_APPLICATION.name -> {
                val current = currentDraftConfigOrDefault(LaunchApplicationActionConfig::class)
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName, appLabel = appLabel))
            }

            else -> {
                val current = currentDraftConfigOrDefault(NotificationReceivedTriggerConfig::class)
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }
        }
    }

    override fun applySelectedWifiTrigger(config: WifiSsidTriggerConfig) {
        applyDraftConfigAndOpenConfiguration(config)
    }

    override fun applySelectedWallpaper(imageUri: String, imageLabel: String) {
        val current = currentDraftConfigOrDefault(SetWallpaperActionConfig::class)
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

    override fun openSelectedCustomConfigurationFlow() {
        val picker = state().pickerState ?: return
        val typeKey = picker.selectedTypeKey ?: return
        definitions.customNavigationEventFor(picker.section, typeKey)?.let(triggerEvent)
    }

    override fun chooseDifferentDefinition() {
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

    private fun defaultConfigurationEvent(picker: DefinitionPickerState, typeKey: String) =
        AutomationEditorEvent.NavigateToDefinitionConfiguration(
            section = picker.section,
            typeKey = typeKey,
            editingIndex = picker.editingIndex,
        )
}
