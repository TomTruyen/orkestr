package com.tomtruyen.orkestr.features.geofence.viewmodel

import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.geofence.data.GeofenceLocation
import com.tomtruyen.orkestr.features.geofence.data.GeofenceLocationRepository
import com.tomtruyen.orkestr.features.geofence.data.GeofenceSearchRepository
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
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

    @Test
    fun selectGeofence_emitsSelectionEventImmediately() = runBlocking {
        val geofence = AutomationGeofence(
            id = "home",
            name = "Home",
            latitude = 51.219448,
            longitude = 4.402464,
            radiusMeters = 150f,
            address = "Antwerp",
        )
        val geofenceRepository = mockk<GeofenceRepository>()
        every { geofenceRepository.observeGeofences() } returns MutableStateFlow(listOf(geofence))
        val geofenceLocationRepository = mockk<GeofenceLocationRepository>()
        coEvery { geofenceLocationRepository.getCurrentLocationOrNull() } returns null
        val viewModel = GeofenceTriggerViewModel(
            stringResolver = resolver,
            geofenceRepository = geofenceRepository,
            geofenceLocationRepository = geofenceLocationRepository,
            geofenceSearchRepository = mockk<GeofenceSearchRepository>(relaxed = true),
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.eventFlow.first() }

        viewModel.onAction(GeofenceTriggerAction.SelectGeofence(geofence.id))

        assertEquals(geofence.id, viewModel.uiState.value.config.geofenceId)
        assertEquals(geofence.name, viewModel.uiState.value.config.geofenceName)
        assertEquals(
            GeofenceTriggerEvent.GeofenceSelected(
                GeofenceTriggerConfig(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                ),
            ),
            event.await(),
        )
    }

    @Test
    fun createGeofenceClicked_withoutExistingGeofence_usesCurrentLocationAsDefault() {
        val geofenceRepository = mockk<GeofenceRepository>()
        every { geofenceRepository.observeGeofences() } returns MutableStateFlow(emptyList())
        val geofenceLocationRepository = mockk<GeofenceLocationRepository>()
        coEvery { geofenceLocationRepository.getCurrentLocationOrNull() } returns GeofenceLocation(
            latitude = 51.219448,
            longitude = 4.402464,
        )
        val viewModel = GeofenceTriggerViewModel(
            stringResolver = resolver,
            geofenceRepository = geofenceRepository,
            geofenceLocationRepository = geofenceLocationRepository,
            geofenceSearchRepository = mockk<GeofenceSearchRepository>(relaxed = true),
        )

        viewModel.onAction(GeofenceTriggerAction.CreateGeofenceClicked)
        waitUntil {
            viewModel.uiState.value.geofenceEditorState?.latitudeText == "51.219448" &&
                viewModel.uiState.value.geofenceEditorState?.longitudeText == "4.402464"
        }

        assertEquals("51.219448", viewModel.uiState.value.geofenceEditorState?.latitudeText)
        assertEquals("4.402464", viewModel.uiState.value.geofenceEditorState?.longitudeText)
    }

    private fun viewModel(): GeofenceTriggerViewModel {
        val geofenceRepository = mockk<GeofenceRepository>()
        every { geofenceRepository.observeGeofences() } returns MutableStateFlow(emptyList())
        val geofenceLocationRepository = mockk<GeofenceLocationRepository>()
        coEvery { geofenceLocationRepository.getCurrentLocationOrNull() } returns null
        return GeofenceTriggerViewModel(
            stringResolver = resolver,
            geofenceRepository = geofenceRepository,
            geofenceLocationRepository = geofenceLocationRepository,
            geofenceSearchRepository = mockk<GeofenceSearchRepository>(relaxed = true),
        )
    }

    private fun waitUntil(timeoutMs: Long = 2_000, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!condition() && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
        check(condition()) { "Condition was not met within ${timeoutMs}ms" }
    }
}
