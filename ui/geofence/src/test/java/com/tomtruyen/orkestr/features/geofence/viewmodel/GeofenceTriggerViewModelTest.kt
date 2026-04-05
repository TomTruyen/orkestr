package com.tomtruyen.orkestr.features.geofence.viewmodel

import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.geofence.data.GeofenceSearchRepository
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GeofenceTriggerViewModelTest {
    private val resolver = object : StringResolver {
        override fun resolve(stringRes: Int, vararg formatArgs: Any): String =
            "res:$stringRes" + if (formatArgs.isEmpty()) "" else ":" + formatArgs.joinToString()
    }

    @Test
    fun saveConfigurationClicked_withoutSelectedGeofence_setsValidationError() {
        val viewModel = viewModel()

        viewModel.onAction(GeofenceTriggerAction.LoadConfig(GeofenceTriggerConfig()))
        viewModel.onAction(GeofenceTriggerAction.SaveConfigurationClicked)

        assertTrue(viewModel.uiState.value.configErrors.isNotEmpty())
    }

    @Test
    fun loadConfig_withValidConfig_updatesUiState() {
        val config = GeofenceTriggerConfig(
            geofenceId = "home",
            geofenceName = "Home",
        )
        val viewModel = viewModel()

        viewModel.onAction(GeofenceTriggerAction.LoadConfig(config))

        assertEquals(config, viewModel.uiState.value.config)
        assertTrue(viewModel.uiState.value.configErrors.isEmpty())
    }

    private fun viewModel(): GeofenceTriggerViewModel {
        val geofenceRepository = mockk<GeofenceRepository>()
        every { geofenceRepository.observeGeofences() } returns MutableStateFlow(emptyList())
        return GeofenceTriggerViewModel(
            stringResolver = resolver,
            geofenceRepository = geofenceRepository,
            geofenceSearchRepository = mockk<GeofenceSearchRepository>(relaxed = true),
        )
    }
}
