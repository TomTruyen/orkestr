package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.model.PackageChangeType
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.PackageChangedTriggerConfig
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class AutomationRuleEditorPackageChangedCustomFlowCoordinatorTest {
    @Test
    fun currentDraftConfigOrDefault_forPackageChanged_returnsDefaultConfig() {
        val coordinator = packageChangedCoordinator()

        assertEquals(
            PackageChangedTriggerConfig(),
            coordinator.currentDraftConfigOrDefault(PackageChangedTriggerConfig::class),
        )
    }

    @Test
    fun applySelectedApp_forPackageChanged_updatesPackageNameAndKeepsChangeType() {
        var state = AutomationEditorUiState(
            pickerState = DefinitionPickerState(
                section = RuleSection.TRIGGERS,
                selectedTypeKey = TriggerType.PACKAGE_CHANGED.name,
                draftConfig = PackageChangedTriggerConfig(
                    packageName = "",
                    changeType = PackageChangeType.UPDATED,
                ),
            ),
        )
        val events = mutableListOf<AutomationEditorEvent>()
        val coordinator = packageChangedCoordinator(
            state = { state },
            updateState = { transform -> state = transform(state) },
            triggerEvent = events::add,
        )

        coordinator.applySelectedApp(
            selectedTypeKey = TriggerType.PACKAGE_CHANGED.name,
            packageName = "com.spotify.music",
            appLabel = "Spotify",
        )

        assertEquals(
            PackageChangedTriggerConfig(
                packageName = "com.spotify.music",
                changeType = PackageChangeType.UPDATED,
            ),
            state.pickerState?.draftConfig,
        )
        assertEquals(
            listOf(
                AutomationEditorEvent.NavigateToDefinitionConfiguration(
                    section = RuleSection.TRIGGERS,
                    typeKey = TriggerType.PACKAGE_CHANGED.name,
                    editingIndex = null,
                ),
            ),
            events,
        )
    }

    @Test
    fun applySelectedApp_forUnknownType_doesNothing() {
        var state = AutomationEditorUiState(
            pickerState = DefinitionPickerState(
                section = RuleSection.TRIGGERS,
                selectedTypeKey = null,
                draftConfig = null,
            ),
        )
        val events = mutableListOf<AutomationEditorEvent>()
        val coordinator = packageChangedCoordinator(
            state = { state },
            updateState = { transform -> state = transform(state) },
            triggerEvent = events::add,
        )

        coordinator.applySelectedApp(
            selectedTypeKey = null,
            packageName = "com.spotify.music",
            appLabel = "Spotify",
        )

        assertNull(state.pickerState?.draftConfig)
        assertEquals(emptyList<AutomationEditorEvent>(), events)
    }

    @Test
    fun openGenericConfigurationFlow_forPackageChanged_navigatesToGenericConfiguration() {
        val state = AutomationEditorUiState(
            pickerState = DefinitionPickerState(
                section = RuleSection.TRIGGERS,
                editingIndex = 1,
                selectedTypeKey = TriggerType.PACKAGE_CHANGED.name,
                draftConfig = PackageChangedTriggerConfig(changeType = PackageChangeType.REMOVED),
            ),
        )
        val events = mutableListOf<AutomationEditorEvent>()
        val coordinator = packageChangedCoordinator(
            state = { state },
            triggerEvent = events::add,
        )

        coordinator.openGenericConfigurationFlow()

        assertEquals(
            listOf(
                AutomationEditorEvent.NavigateToDefinitionConfiguration(
                    section = RuleSection.TRIGGERS,
                    typeKey = TriggerType.PACKAGE_CHANGED.name,
                    editingIndex = 1,
                ),
            ),
            events,
        )
    }
}
