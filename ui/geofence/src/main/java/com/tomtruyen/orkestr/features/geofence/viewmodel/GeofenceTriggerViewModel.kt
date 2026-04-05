package com.tomtruyen.orkestr.features.geofence.viewmodel

import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.geofence.data.GeofenceLocationRepository
import com.tomtruyen.orkestr.features.geofence.data.GeofenceSearchRepository
import com.tomtruyen.orkestr.features.geofence.state.GeofenceEditorState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerEvent
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerUiState
import com.tomtruyen.orkestr.ui.geofence.R
import java.util.Locale
import java.util.UUID
import com.tomtruyen.automation.R as AutomationR

class GeofenceTriggerViewModel(
    private val stringResolver: StringResolver,
    private val geofenceRepository: GeofenceRepository,
    private val geofenceLocationRepository: GeofenceLocationRepository,
    private val geofenceSearchRepository: GeofenceSearchRepository,
) : BaseViewModel<GeofenceTriggerUiState, GeofenceTriggerEvent, GeofenceTriggerAction>(
    initialState = GeofenceTriggerUiState(),
) {
    init {
        launch {
            geofenceRepository.observeGeofences().collect { geofences ->
                updateState { state -> state.copy(geofences = geofences) }
            }
        }
    }

    override fun onAction(action: GeofenceTriggerAction) {
        when (action) {
            is GeofenceTriggerAction.LoadConfig -> loadConfig(action.config)

            is GeofenceTriggerAction.SelectGeofence -> selectGeofence(action.geofenceId)

            is GeofenceTriggerAction.SelectTransition -> Unit

            GeofenceTriggerAction.SaveConfigurationClicked -> saveConfiguration()

            GeofenceTriggerAction.CreateGeofenceClicked -> openGeofenceEditor()

            GeofenceTriggerAction.CloseGeofenceEditorClicked -> closeGeofenceEditor()

            is GeofenceTriggerAction.GeofenceNameChanged -> updateEditorState {
                it.copy(name = action.value, errors = emptyList())
            }

            is GeofenceTriggerAction.GeofenceLatitudeChanged -> updateLatitude(action.value)

            is GeofenceTriggerAction.GeofenceLongitudeChanged -> updateLongitude(action.value)

            is GeofenceTriggerAction.GeofenceRadiusChanged -> updateRadius(action.value)

            is GeofenceTriggerAction.GeofenceAddressQueryChanged -> updateEditorState {
                it.copy(addressQuery = action.value, errors = emptyList())
            }

            GeofenceTriggerAction.GeofenceAddressSearchClicked -> searchAddresses()

            is GeofenceTriggerAction.GeofenceSearchResultSelected -> applySearchResult(action.result)

            is GeofenceTriggerAction.GeofenceMapLocationSelected -> updateMapLocation(action.latitude, action.longitude)

            GeofenceTriggerAction.SaveGeofenceClicked -> saveGeofence()
        }
    }

    private fun loadConfig(config: GeofenceTriggerConfig) {
        updateState { state ->
            state.copy(
                config = config,
                configErrors = emptyList(),
                geofenceEditorState = null,
            )
        }
    }

    private fun selectGeofence(geofenceId: String) {
        val geofence = uiState.value.geofences.firstOrNull { it.id == geofenceId } ?: return
        updateState { state ->
            state.copy(
                config = state.config.copy(
                    geofenceId = geofence.id,
                    geofenceName = geofence.name,
                ),
                configErrors = emptyList(),
            )
        }
    }

    private fun saveConfiguration() {
        val config = uiState.value.config
        if (config.geofenceId.isBlank()) {
            updateState { state ->
                state.copy(
                    configErrors = listOf(
                        stringResolver.resolve(
                            AutomationR.string.automation_definition_trigger_geofence_error_missing_geofence,
                        ),
                    ),
                )
            }
            return
        }

        triggerEvent(GeofenceTriggerEvent.GeofenceSelected(config))
    }

    private fun openGeofenceEditor() {
        val selectedConfig = uiState.value.config
        val sourceGeofence = uiState.value.geofences.firstOrNull { it.id == selectedConfig.geofenceId }
            ?: uiState.value.geofences.firstOrNull()
        val defaultLatitude = sourceGeofence?.latitude ?: DEFAULT_LATITUDE
        val defaultLongitude = sourceGeofence?.longitude ?: DEFAULT_LONGITUDE
        val defaultRadius = sourceGeofence?.radiusMeters ?: DEFAULT_RADIUS_METERS

        updateState { state ->
            state.copy(
                geofenceEditorState = GeofenceEditorState(
                    id = UUID.randomUUID().toString(),
                    latitudeText = formatCoordinate(defaultLatitude),
                    longitudeText = formatCoordinate(defaultLongitude),
                    radiusText = formatRadius(defaultRadius),
                    mapLatitude = defaultLatitude,
                    mapLongitude = defaultLongitude,
                    mapRadiusMeters = defaultRadius,
                ),
            )
        }
        triggerEvent(GeofenceTriggerEvent.NavigateToGeofenceEditor)

        if (sourceGeofence == null) {
            launch {
                val currentLocation = geofenceLocationRepository.getCurrentLocationOrNull() ?: return@launch
                updateEditorState { state ->
                    if (
                        state.latitudeText != formatCoordinate(DEFAULT_LATITUDE) ||
                        state.longitudeText != formatCoordinate(DEFAULT_LONGITUDE)
                    ) {
                        state
                    } else {
                        state.copy(
                            latitudeText = formatCoordinate(currentLocation.latitude),
                            longitudeText = formatCoordinate(currentLocation.longitude),
                            mapLatitude = currentLocation.latitude,
                            mapLongitude = currentLocation.longitude,
                        )
                    }
                }
            }
        }
    }

    private fun closeGeofenceEditor() {
        updateState { state -> state.copy(geofenceEditorState = null) }
        triggerEvent(GeofenceTriggerEvent.PopBack)
    }

    private fun updateEditorState(transform: (GeofenceEditorState) -> GeofenceEditorState) {
        val editorState = uiState.value.geofenceEditorState ?: return
        updateState { state -> state.copy(geofenceEditorState = transform(editorState)) }
    }

    private fun updateLatitude(value: String) {
        updateEditorState { state ->
            val parsed = value.toDoubleOrNull()
            state.copy(
                latitudeText = value,
                mapLatitude = parsed ?: state.mapLatitude,
                errors = emptyList(),
            )
        }
    }

    private fun updateLongitude(value: String) {
        updateEditorState { state ->
            val parsed = value.toDoubleOrNull()
            state.copy(
                longitudeText = value,
                mapLongitude = parsed ?: state.mapLongitude,
                errors = emptyList(),
            )
        }
    }

    private fun updateRadius(value: String) {
        updateEditorState { state ->
            val parsed = value.toFloatOrNull()
            state.copy(
                radiusText = value,
                mapRadiusMeters = parsed ?: state.mapRadiusMeters,
                errors = emptyList(),
            )
        }
    }

    private fun updateMapLocation(latitude: Double, longitude: Double) {
        updateEditorState { state ->
            state.copy(
                latitudeText = formatCoordinate(latitude),
                longitudeText = formatCoordinate(longitude),
                mapLatitude = latitude,
                mapLongitude = longitude,
                errors = emptyList(),
            )
        }
    }

    private fun applySearchResult(result: GeofenceSearchResult) {
        updateEditorState { state ->
            state.copy(
                addressQuery = result.title,
                selectedAddress = result.title,
                searchResults = emptyList(),
                latitudeText = formatCoordinate(result.latitude),
                longitudeText = formatCoordinate(result.longitude),
                mapLatitude = result.latitude,
                mapLongitude = result.longitude,
                errors = emptyList(),
            )
        }
    }

    private fun searchAddresses() {
        val editorState = uiState.value.geofenceEditorState ?: return
        if (editorState.addressQuery.isBlank()) return

        launch {
            if (!geofenceSearchRepository.isAvailable()) {
                updateEditorErrors(listOf(stringResolver.resolve(R.string.geofence_error_geocoder_unavailable)))
                return@launch
            }

            val results = runCatching { geofenceSearchRepository.search(editorState.addressQuery.trim()) }.getOrElse {
                emptyList()
            }

            updateEditorState { state ->
                state.copy(
                    searchResults = results,
                    errors = if (results.isEmpty()) {
                        listOf(stringResolver.resolve(R.string.geofence_error_geocoder_no_results))
                    } else {
                        emptyList()
                    },
                )
            }
        }
    }

    private fun saveGeofence() {
        val editorState = uiState.value.geofenceEditorState ?: return
        val geofence = editorState.toDomainOrNull()
        val errors = validateGeofence(editorState)
        if (geofence == null || errors.isNotEmpty()) {
            updateEditorErrors(errors)
            return
        }

        launch {
            geofenceRepository.upsertGeofence(geofence)
            updateState { state ->
                state.copy(
                    config = state.config.copy(
                        geofenceId = geofence.id,
                        geofenceName = geofence.name,
                    ),
                    geofenceEditorState = null,
                    configErrors = emptyList(),
                )
            }
            triggerEvent(GeofenceTriggerEvent.PopBack)
        }
    }

    private fun updateEditorErrors(errors: List<String>) {
        updateEditorState { state -> state.copy(errors = errors) }
    }

    private fun validateGeofence(editorState: GeofenceEditorState): List<String> {
        val latitude = editorState.latitudeText.toDoubleOrNull()
        val longitude = editorState.longitudeText.toDoubleOrNull()
        val radius = editorState.radiusText.toFloatOrNull()
        val errors = mutableListOf<String>()

        if (editorState.name.isBlank()) {
            errors += stringResolver.resolve(R.string.geofence_error_name_required)
        }
        if (latitude == null || latitude !in -90.0..90.0) {
            errors += stringResolver.resolve(R.string.geofence_error_latitude_invalid)
        }
        if (longitude == null || longitude !in -180.0..180.0) {
            errors += stringResolver.resolve(R.string.geofence_error_longitude_invalid)
        }
        if (radius == null || radius <= 0f) {
            errors += stringResolver.resolve(R.string.geofence_error_radius_invalid)
        }

        return errors
    }

    private fun GeofenceEditorState.toDomainOrNull(): AutomationGeofence? {
        val latitude = latitudeText.toDoubleOrNull() ?: return null
        val longitude = longitudeText.toDoubleOrNull() ?: return null
        val radius = radiusText.toFloatOrNull() ?: return null
        return AutomationGeofence(
            id = id,
            name = name.trim(),
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radius,
            address = selectedAddress ?: addressQuery.takeIf { it.isNotBlank() },
        )
    }

    private fun formatCoordinate(value: Double): String = String.format(Locale.US, "%.6f", value)

    private fun formatRadius(value: Float): String = String.format(Locale.US, "%.0f", value)

    private companion object {
        const val DEFAULT_LATITUDE = 0.0
        const val DEFAULT_LONGITUDE = 0.0
        const val DEFAULT_RADIUS_METERS = 150f
    }
}
