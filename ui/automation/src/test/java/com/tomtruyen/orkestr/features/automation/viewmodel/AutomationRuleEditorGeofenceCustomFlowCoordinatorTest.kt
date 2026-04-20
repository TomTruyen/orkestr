package com.tomtruyen.orkestr.features.automation.viewmodel

import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.features.constraints.ConstraintType
import com.tomtruyen.automation.features.constraints.config.GeofenceConstraintConfig
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationEditorUiState
import com.tomtruyen.orkestr.features.automation.state.DefinitionPickerState
import com.tomtruyen.orkestr.features.automation.state.RuleSection
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AutomationRuleEditorGeofenceCustomFlowCoordinatorTest {
    @Test
    fun applySelectedGeofence_forConstraintStoresConstraintDraftAndPreservesInsideSetting() {
        val geofence = AutomationGeofence(
            id = "home",
            name = "Home",
            latitude = 51.219448,
            longitude = 4.402464,
            radiusMeters = 200f,
            address = "Antwerp",
        )
        var state = AutomationEditorUiState(
            pickerState = DefinitionPickerState(
                section = RuleSection.CONSTRAINTS,
                selectedTypeKey = ConstraintType.GEOFENCE.name,
                draftConfig = GeofenceConstraintConfig(inside = false),
            ),
        )
        val events = mutableListOf<AutomationEditorEvent>()
        val coordinator = packageChangedCoordinator(
            state = { state },
            updateState = { transform -> state = transform(state) },
            triggerEvent = events::add,
        )

        coordinator.applySelectedGeofence(geofence)

        assertEquals(ConstraintType.GEOFENCE.name, state.pickerState?.selectedTypeKey)
        assertEquals(
            GeofenceConstraintConfig(
                geofenceId = "home",
                geofenceName = "Home",
                latitude = 51.219448,
                longitude = 4.402464,
                radiusMeters = 200f,
                inside = false,
            ),
            state.pickerState?.draftConfig,
        )
        assertEquals(
            listOf(
                AutomationEditorEvent.NavigateToDefinitionConfiguration(
                    section = RuleSection.CONSTRAINTS,
                    typeKey = ConstraintType.GEOFENCE.name,
                    editingIndex = null,
                ),
            ),
            events,
        )
    }
}
