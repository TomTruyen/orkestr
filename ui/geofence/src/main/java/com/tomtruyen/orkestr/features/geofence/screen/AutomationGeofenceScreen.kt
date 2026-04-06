package com.tomtruyen.orkestr.features.geofence.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationSearchField
import com.tomtruyen.orkestr.common.component.AutomationSectionHeader
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.features.geofence.component.GeofenceMapPreviewCard
import com.tomtruyen.orkestr.features.geofence.component.GeofenceSearchResultCard
import com.tomtruyen.orkestr.features.geofence.component.GeofenceSelectionCard
import com.tomtruyen.orkestr.features.geofence.component.GoogleMapPicker
import com.tomtruyen.orkestr.features.geofence.component.MapPickerSheetContent
import com.tomtruyen.orkestr.features.geofence.component.geofenceMapHeroModifier
import com.tomtruyen.orkestr.features.geofence.state.GeofenceTriggerAction
import com.tomtruyen.orkestr.features.geofence.viewmodel.GeofenceTriggerViewModel
import com.tomtruyen.orkestr.ui.geofence.R

@Composable
fun AutomationGeofenceConfigurationScreen(viewModel: GeofenceTriggerViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val config = uiState.config

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AutomationGeofenceEditorScreen(viewModel: GeofenceTriggerViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState.geofenceEditorState ?: return

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                        AutomationSearchField(
                            value = state.addressQuery,
                            onValueChange = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceAddressQueryChanged(it))
                            },
                            onSearchClick = {
                                viewModel.onAction(GeofenceTriggerAction.GeofenceAddressSearchClicked)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = stringResource(R.string.geofence_search_label),
                            placeholder = stringResource(R.string.geofence_search_placeholder),
                        )
                        state.selectedAddress?.let { address ->
                            Text(
                                text = stringResource(R.string.geofence_selected_address, address),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (state.searchResults.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.geofence_search_results_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            state.searchResults.forEach { result ->
                                GeofenceSearchResultCard(
                                    result = result,
                                    onClick = {
                                        viewModel.onAction(
                                            GeofenceTriggerAction.GeofenceSearchResultSelected(result),
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }

            item {
                GeofenceMapPreviewCard(
                    state = state,
                    onOpenMapPicker = {
                        viewModel.onAction(GeofenceTriggerAction.OpenMapPickerClicked)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AutomationGeofenceMapPickerScreen(viewModel: GeofenceTriggerViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val mapPickerState = uiState.mapPickerState ?: return
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true,
    )

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        sheetPeekHeight = 260.dp,
        sheetDragHandle = null,
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = bottomSheetState,
        ),
        sheetContent = {
            MapPickerSheetContent(
                state = mapPickerState,
                onSearchQueryChanged = {
                    viewModel.onAction(GeofenceTriggerAction.MapPickerAddressQueryChanged(it))
                },
                onSearchClicked = {
                    viewModel.onAction(GeofenceTriggerAction.MapPickerAddressSearchClicked)
                },
                onSearchResultClicked = { result ->
                    viewModel.onAction(GeofenceTriggerAction.MapPickerSearchResultSelected(result))
                },
                onConfirmClicked = {
                    viewModel.onAction(GeofenceTriggerAction.ConfirmMapPickerClicked)
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            GoogleMapPicker(
                state = mapPickerState,
                modifier = geofenceMapHeroModifier().fillMaxSize(),
                onMapClick = { latitude, longitude ->
                    viewModel.onAction(GeofenceTriggerAction.MapPickerLocationSelected(latitude, longitude))
                },
            )
        }
    }
}
