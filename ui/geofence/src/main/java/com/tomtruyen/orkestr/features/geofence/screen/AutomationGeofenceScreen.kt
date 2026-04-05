package com.tomtruyen.orkestr.features.geofence.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationSectionHeader
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.ui.geofence.R
import com.tomtruyen.orkestr.features.geofence.state.GeofenceEditorState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.viewmodel.GeofenceTriggerViewModel
import java.util.Locale

@Composable
fun AutomationGeofenceConfigurationScreen(viewModel: GeofenceTriggerViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val config = uiState.config

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card {
                Button(
                    onClick = { viewModel.onAction(GeofenceTriggerAction.SaveConfigurationClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(stringResource(R.string.geofence_action_save_trigger))
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        AutomationSectionHeader(
                            title = stringResource(R.string.geofence_saved_title),
                            description = stringResource(R.string.geofence_saved_description),
                        )
                        OutlinedButton(
                            onClick = { viewModel.onAction(GeofenceTriggerAction.CreateGeofenceClicked) },
                        ) {
                            Text(stringResource(R.string.geofence_action_create_geofence))
                        }
                    }
                }
            }

            if (uiState.geofences.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = stringResource(R.string.geofence_none_title),
                        description = stringResource(R.string.geofence_none_description),
                    )
                }
            } else {
                items(uiState.geofences, key = { it.id }) { geofence ->
                    GeofenceSelectionCard(
                        geofence = geofence,
                        selected = geofence.id == config.geofenceId,
                        onClick = {
                            viewModel.onAction(GeofenceTriggerAction.SelectGeofence(geofence.id))
                        },
                    )
                }
            }

            item {
                if (uiState.configErrors.isNotEmpty()) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        AutomationCardColumn {
                            ValidationCard(errors = uiState.configErrors)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AutomationGeofenceEditorScreen(viewModel: GeofenceTriggerViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState.geofenceEditorState ?: return

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card {
                Button(
                    onClick = { viewModel.onAction(GeofenceTriggerAction.SaveGeofenceClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(stringResource(R.string.geofence_action_create_geofence))
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        AutomationSectionHeader(
                            title = stringResource(R.string.geofence_create_title),
                            description = stringResource(R.string.geofence_create_description),
                        )
                    }
                }
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceNameChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.geofence_name_label)) },
                            placeholder = { Text(stringResource(R.string.geofence_name_placeholder)) },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = state.radiusText,
                            onValueChange = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceRadiusChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.geofence_radius_label)) },
                            placeholder = { Text(stringResource(R.string.geofence_radius_placeholder)) },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = state.latitudeText,
                            onValueChange = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceLatitudeChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.geofence_latitude_label)) },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = state.longitudeText,
                            onValueChange = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceLongitudeChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.geofence_longitude_label)) },
                            singleLine = true,
                        )
                        if (state.errors.isNotEmpty()) {
                            ValidationCard(errors = state.errors)
                        }
                    }
                }
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        OutlinedTextField(
                            value = state.addressQuery,
                            onValueChange = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceAddressQueryChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.geofence_search_label)) },
                            placeholder = {
                                Text(stringResource(R.string.geofence_search_placeholder))
                            },
                            singleLine = true,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.onAction(GeofenceTriggerAction.GeofenceAddressSearchClicked) },
                            ) {
                                Text(stringResource(R.string.geofence_action_search))
                            }
                        }
                        state.selectedAddress?.let { address ->
                            Text(
                                text = stringResource(R.string.geofence_selected_address, address),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            if (state.searchResults.isNotEmpty()) {
                item {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        AutomationCardColumn {
                            Text(
                                text = stringResource(R.string.geofence_search_results_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
                items(state.searchResults, key = { it.title }) { result ->
                    GeofenceSearchResultCard(
                        result = result,
                        onClick = { viewModel.onAction(GeofenceTriggerAction.GeofenceSearchResultSelected(result)) },
                    )
                }
            }

            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    AutomationCardColumn {
                        GoogleGeofenceMap(
                            state = state,
                            onMapClick = { latitude, longitude ->
                                viewModel.onAction(GeofenceTriggerAction.GeofenceMapLocationSelected(latitude, longitude))
                            },
                        )
                        Text(
                            text = stringResource(R.string.geofence_map_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeofenceSelectionCard(
    geofence: AutomationGeofence,
    selected: Boolean,
    onClick: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        AutomationCardColumn {
            Text(
                text = geofence.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            geofence.address?.takeIf { it.isNotBlank() }?.let { address ->
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = formatCoordinates(geofence.latitude, geofence.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = String.format(
                    Locale.US,
                    "%.0f m",
                    geofence.radiusMeters,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GeofenceSearchResultCard(result: GeofenceSearchResult, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        AutomationCardColumn {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatCoordinates(result.latitude, result.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GoogleGeofenceMap(
    state: GeofenceEditorState,
    onMapClick: (Double, Double) -> Unit,
) {
    val selectedLatLng = LatLng(state.mapLatitude, state.mapLongitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, 14f)
    }
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded, state.mapLatitude, state.mapLongitude) {
        if (!mapLoaded) return@LaunchedEffect
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLng(selectedLatLng),
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
        onMapLoaded = { mapLoaded = true },
        onMapClick = { point -> onMapClick(point.latitude, point.longitude) },
    ) {
        Marker(
            state = MarkerState(position = selectedLatLng),
            title = state.name.ifBlank { stringResource(R.string.geofence_create_title) },
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
        )
        Circle(
            center = selectedLatLng,
            radius = state.mapRadiusMeters.toDouble(),
            fillColor = Color(0x332196F3),
            strokeColor = Color(0xFF1976D2),
            strokeWidth = 3f,
        )
    }
}

private fun formatCoordinates(latitude: Double, longitude: Double): String =
    String.format(Locale.US, "%.6f, %.6f", latitude, longitude)
