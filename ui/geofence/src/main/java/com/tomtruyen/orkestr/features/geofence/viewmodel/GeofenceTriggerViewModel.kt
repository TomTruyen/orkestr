package com.tomtruyen.orkestr.features.geofence.viewmodel

import com.tomtruyen.automation.data.repository.GeofenceRepository
import com.tomtruyen.automation.features.triggers.config.GeofenceTriggerConfig
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.common.StringResolver
import com.tomtruyen.orkestr.features.geofence.data.GeofenceLocationRepository
import com.tomtruyen.orkestr.features.geofence.data.GeofenceSearchRepository
import com.tomtruyen.orkestr.features.geofence.state.GeofenceEditorState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceMapPickerState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerEvent
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerUiState
import com.tomtruyen.orkestr.ui.geofence.R
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
            GeofenceTriggerAction.SaveConfigurationClicked -> saveConfiguration()
            GeofenceTriggerAction.CreateGeofenceClicked -> openGeofenceEditor()
            GeofenceTriggerAction.CloseGeofenceEditorClicked -> closeGeofenceEditor()
            GeofenceTriggerAction.OpenMapPickerClicked -> openMapPicker()
            GeofenceTriggerAction.CloseMapPickerClicked -> closeMapPicker()
            else -> handleScreenAction(action)
        }
    }

    private fun handleScreenAction(action: GeofenceTriggerAction) {
        when (action) {
            is GeofenceTriggerAction.GeofenceNameChanged -> updateEditorState { it.withName(action.value) }

            is GeofenceTriggerAction.GeofenceLatitudeChanged -> updateEditorState { it.withLatitudeInput(action.value) }

            is GeofenceTriggerAction.GeofenceLongitudeChanged -> updateEditorState {
                it.withLongitudeInput(
                    action.value,
                )
            }

            is GeofenceTriggerAction.GeofenceRadiusChanged -> updateEditorState { it.withRadiusInput(action.value) }

            is GeofenceTriggerAction.GeofenceAddressQueryChanged -> updateEditorState {
                it.withAddressQuery(
                    action.value,
                )
            }

            GeofenceTriggerAction.GeofenceAddressSearchClicked -> searchAddresses()

            is GeofenceTriggerAction.GeofenceSearchResultSelected -> applySearchResult(action.result)

            is GeofenceTriggerAction.GeofenceMapLocationSelected -> updateMapLocation(action.latitude, action.longitude)

            is GeofenceTriggerAction.MapPickerAddressQueryChanged -> updateMapPickerState {
                it.withAddressQuery(
                    action.value,
                )
            }

            GeofenceTriggerAction.MapPickerAddressSearchClicked -> searchMapPickerAddresses()

            is GeofenceTriggerAction.MapPickerSearchResultSelected -> applyMapPickerSearchResult(action.result)

            is GeofenceTriggerAction.MapPickerLocationSelected -> updateMapPickerLocation(
                action.latitude,
                action.longitude,
            )

            GeofenceTriggerAction.ConfirmMapPickerClicked -> confirmMapPicker()

            GeofenceTriggerAction.SaveGeofenceClicked -> saveGeofence()

            is GeofenceTriggerAction.SelectTransition -> Unit

            is GeofenceTriggerAction.LoadConfig,
            is GeofenceTriggerAction.SelectGeofence,
            GeofenceTriggerAction.SaveConfigurationClicked,
            GeofenceTriggerAction.CreateGeofenceClicked,
            GeofenceTriggerAction.CloseGeofenceEditorClicked,
            GeofenceTriggerAction.OpenMapPickerClicked,
            GeofenceTriggerAction.CloseMapPickerClicked,
            -> Unit
        }
    }

    private fun loadConfig(config: GeofenceTriggerConfig) {
        updateState { state ->
            state.copy(
                config = config,
                configErrors = emptyList(),
                geofenceEditorState = null,
                mapPickerState = null,
            )
        }
    }

    private fun selectGeofence(geofenceId: String) {
        val geofence = uiState.value.geofences.firstOrNull { it.id == geofenceId } ?: return
        val updatedConfig = uiState.value.config.copy(
            geofenceId = geofence.id,
            geofenceName = geofence.name,
        )
        updateState { state ->
            state.copy(
                config = updatedConfig,
                configErrors = emptyList(),
            )
        }
        triggerEvent(GeofenceTriggerEvent.GeofenceSelected(updatedConfig))
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
                geofenceEditorState = defaultEditorState(
                    id = UUID.randomUUID().toString(),
                    latitude = defaultLatitude,
                    longitude = defaultLongitude,
                    radius = defaultRadius,
                ),
            )
        }
        triggerEvent(GeofenceTriggerEvent.NavigateToGeofenceEditor)

        if (sourceGeofence == null) {
            launch {
                val currentLocation = geofenceLocationRepository.getCurrentLocationOrNull() ?: return@launch
                updateEditorState { state ->
                    if (
                        state.latitudeText != DEFAULT_LATITUDE.formatCoordinate() ||
                        state.longitudeText != DEFAULT_LONGITUDE.formatCoordinate()
                    ) {
                        state
                    } else {
                        state.copy(
                            latitudeText = currentLocation.latitude.formatCoordinate(),
                            longitudeText = currentLocation.longitude.formatCoordinate(),
                            mapLatitude = currentLocation.latitude,
                            mapLongitude = currentLocation.longitude,
                        )
                    }
                }
            }
        }
    }

    private fun closeGeofenceEditor() {
        updateState { state -> state.copy(geofenceEditorState = null, mapPickerState = null) }
        triggerEvent(GeofenceTriggerEvent.PopBack)
    }

    private fun openMapPicker() {
        val editorState = uiState.value.geofenceEditorState ?: return
        updateState { state ->
            state.copy(mapPickerState = editorState.toMapPickerState())
        }
        triggerEvent(GeofenceTriggerEvent.NavigateToMapPicker)
    }

    private fun closeMapPicker() {
        updateState { state -> state.copy(mapPickerState = null) }
        triggerEvent(GeofenceTriggerEvent.PopBack)
    }

    private fun updateEditorState(transform: (GeofenceEditorState) -> GeofenceEditorState) {
        val editorState = uiState.value.geofenceEditorState ?: return
        updateState { state -> state.copy(geofenceEditorState = transform(editorState)) }
    }

    private fun updateMapPickerState(transform: (GeofenceMapPickerState) -> GeofenceMapPickerState) {
        val mapPickerState = uiState.value.mapPickerState ?: return
        updateState { state -> state.copy(mapPickerState = transform(mapPickerState)) }
    }

    private fun updateMapLocation(latitude: Double, longitude: Double) {
        updateEditorState { it.withMapLocation(latitude, longitude) }
    }

    private fun applySearchResult(result: GeofenceSearchResult) {
        updateEditorState { it.withSearchResult(result) }
    }

    private fun updateMapPickerLocation(latitude: Double, longitude: Double) {
        updateMapPickerState { it.withLocation(latitude, longitude) }
    }

    private fun applyMapPickerSearchResult(result: GeofenceSearchResult) {
        updateMapPickerState { it.withSearchResult(result) }
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

            updateEditorState {
                it.withSearchOutcome(results, stringResolver.resolve(R.string.geofence_error_geocoder_no_results))
            }
        }
    }

    private fun searchMapPickerAddresses() {
        val mapPickerState = uiState.value.mapPickerState ?: return
        if (mapPickerState.addressQuery.isBlank()) return

        launch {
            if (!geofenceSearchRepository.isAvailable()) {
                updateMapPickerErrors(listOf(stringResolver.resolve(R.string.geofence_error_geocoder_unavailable)))
                return@launch
            }

            val results = runCatching {
                geofenceSearchRepository.search(
                    mapPickerState.addressQuery.trim(),
                )
            }.getOrElse {
                emptyList()
            }

            updateMapPickerState {
                it.withSearchOutcome(results, stringResolver.resolve(R.string.geofence_error_geocoder_no_results))
            }
        }
    }

    private fun saveGeofence() {
        val editorState = uiState.value.geofenceEditorState ?: return
        val geofence = editorState.toDomainOrNull()
        val errors = editorState.validate(stringResolver::resolve)
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
                    mapPickerState = null,
                    configErrors = emptyList(),
                )
            }
            triggerEvent(GeofenceTriggerEvent.PopBack)
        }
    }

    private fun updateEditorErrors(errors: List<String>) {
        updateEditorState { it.withErrors(errors) }
    }

    private fun updateMapPickerErrors(errors: List<String>) {
        updateMapPickerState { it.withErrors(errors) }
    }

    private fun confirmMapPicker() {
        val mapPickerState = uiState.value.mapPickerState ?: return
        updateEditorState { it.applyMapPicker(mapPickerState) }
        closeMapPicker()
    }

    private companion object {
        const val DEFAULT_LATITUDE = 0.0
        const val DEFAULT_LONGITUDE = 0.0
        const val DEFAULT_RADIUS_METERS = 150f
    }
}
