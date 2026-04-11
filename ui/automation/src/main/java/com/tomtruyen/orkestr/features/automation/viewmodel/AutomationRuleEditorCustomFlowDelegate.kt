package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationDefinitionRegistry
import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.actions.config.SetWallpaperActionConfig
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import com.tomtruyen.automation.features.constraints.config.TimeOfDayConstraintConfig
import com.tomtruyen.automation.features.constraints.config.WifiSsidConstraintConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.automation.features.triggers.config.PackageChangedTriggerConfig
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
    fun applySelectedGeofence(geofence: AutomationGeofence)
    fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String)
    fun applySelectedWifi(config: WifiSsidTriggerConfig)
    fun applySelectedWallpaper(imageUri: String, imageLabel: String)
    fun openSelectedCustomConfigurationFlow()
    fun openGenericConfigurationFlow()
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

    override fun applySelectedGeofence(geofence: AutomationGeofence) = requireDelegate().applySelectedGeofence(geofence)

    override fun applySelectedApp(selectedTypeKey: String?, packageName: String, appLabel: String) =
        requireDelegate().applySelectedApp(selectedTypeKey, packageName, appLabel)

    override fun applySelectedWifi(config: WifiSsidTriggerConfig) = requireDelegate().applySelectedWifi(config)

    override fun applySelectedWallpaper(imageUri: String, imageLabel: String) =
        requireDelegate().applySelectedWallpaper(imageUri, imageLabel)

    override fun openSelectedCustomConfigurationFlow() = requireDelegate().openSelectedCustomConfigurationFlow()

    override fun openGenericConfigurationFlow() = requireDelegate().openGenericConfigurationFlow()

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
            PackageChangedTriggerConfig::class -> PackageChangedTriggerConfig()
            ApplicationLifecycleTriggerConfig::class -> ApplicationLifecycleTriggerConfig()
            LaunchApplicationActionConfig::class -> LaunchApplicationActionConfig()
            SetWallpaperActionConfig::class -> SetWallpaperActionConfig()
            WifiSsidTriggerConfig::class -> WifiSsidTriggerConfig()
            TimeBasedTriggerConfig::class -> TimeBasedTriggerConfig()
            TimeOfDayConstraintConfig::class -> TimeOfDayConstraintConfig()
            GeofenceConstraintConfig::class -> GeofenceConstraintConfig()
            WifiSsidConstraintConfig::class -> WifiSsidConstraintConfig()
            else -> error("Unsupported config class: ${configClass.qualifiedName}")
        }

        @Suppress("UNCHECKED_CAST")
        return defaultConfig as T
    }

    override fun applySelectedGeofence(geofence: AutomationGeofence) {
        val picker = state().pickerState ?: return
        val config = when (picker.selectedTypeKey) {
            TriggerType.GEOFENCE.name -> GeofenceTriggerConfig(
                geofenceId = geofence.id,
                geofenceName = geofence.name,
            )

            else -> GeofenceConstraintConfig(
                geofenceId = geofence.id,
                geofenceName = geofence.name,
                latitude = geofence.latitude,
                longitude = geofence.longitude,
                radiusMeters = geofence.radiusMeters,
            )
        }
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

            TriggerType.NOTIFICATION_RECEIVED.name -> {
                val current = currentDraftConfigOrDefault(NotificationReceivedTriggerConfig::class)
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }

            TriggerType.PACKAGE_CHANGED.name -> {
                val current = currentDraftConfigOrDefault(PackageChangedTriggerConfig::class)
                applyDraftConfigAndOpenConfiguration(current.copy(packageName = packageName))
            }
        }
    }

    override fun applySelectedWifi(config: WifiSsidTriggerConfig) {
        val picker = state().pickerState ?: return
        val configToApply = when (picker.selectedTypeKey) {
            TriggerType.WIFI_SSID_IN_RANGE.name -> config
            else -> WifiSsidConstraintConfig(ssid = config.ssid.trim())
        }
        applyDraftConfigAndOpenConfiguration(configToApply)
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

    override fun openGenericConfigurationFlow() {
        val picker = state().pickerState ?: return
        val typeKey = picker.selectedTypeKey ?: return
        triggerEvent(defaultConfigurationEvent(picker, typeKey))
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
