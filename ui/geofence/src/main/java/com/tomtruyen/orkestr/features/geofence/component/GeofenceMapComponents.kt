package com.tomtruyen.orkestr.features.geofence.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
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
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationSearchField
import com.tomtruyen.orkestr.common.component.LocalNavigationSharedTransitionScope
import com.tomtruyen.orkestr.common.component.ValidationCard
import com.tomtruyen.orkestr.features.geofence.state.GeofenceEditorState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceMapPickerState
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import com.tomtruyen.orkestr.ui.geofence.R

@Composable
fun GeofenceMapPreviewCard(state: GeofenceEditorState, onOpenMapPicker: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenMapPicker),
    ) {
        AutomationCardColumn {
            Box(
                modifier = geofenceMapHeroModifier()
                    .fillMaxWidth()
                    .height(PREVIEW_MAP_HEIGHT),
            ) {
                GoogleGeofenceMap(
                    state = state,
                    interactive = false,
                    modifier = Modifier.fillMaxSize(),
                    onMapClick = { _, _ -> onOpenMapPicker() },
                )
            }
            Text(
                text = stringResource(R.string.geofence_map_preview_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun GoogleGeofenceMap(
    state: GeofenceEditorState,
    interactive: Boolean,
    modifier: Modifier = Modifier,
    onMapClick: (Double, Double) -> Unit,
) {
    val selectedLatLng = LatLng(state.mapLatitude, state.mapLongitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, PREVIEW_MAP_ZOOM)
    }
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded, state.mapLatitude, state.mapLongitude) {
        if (!mapLoaded) return@LaunchedEffect
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(selectedLatLng))
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = interactive,
            myLocationButtonEnabled = false,
            scrollGesturesEnabled = interactive,
            zoomGesturesEnabled = interactive,
        ),
        onMapLoaded = { mapLoaded = true },
        onMapClick = { point ->
            if (interactive) {
                onMapClick(point.latitude, point.longitude)
            } else {
                onMapClick(state.mapLatitude, state.mapLongitude)
            }
        },
    ) {
        Marker(
            state = MarkerState(position = selectedLatLng),
            title = state.name.ifBlank { stringResource(R.string.geofence_create_title) },
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
        )
        Circle(
            center = selectedLatLng,
            radius = state.mapRadiusMeters.toDouble(),
            fillColor = GEOFENCE_FILL_COLOR,
            strokeColor = GEOFENCE_STROKE_COLOR,
            strokeWidth = GEOFENCE_STROKE_WIDTH,
        )
    }
}

@Composable
fun GoogleMapPicker(
    state: GeofenceMapPickerState,
    modifier: Modifier = Modifier,
    onMapClick: (Double, Double) -> Unit,
) {
    val selectedLatLng = LatLng(state.latitude, state.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, PICKER_MAP_ZOOM)
    }
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded, state.latitude, state.longitude) {
        if (!mapLoaded) return@LaunchedEffect
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(selectedLatLng))
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
        ),
        onMapLoaded = { mapLoaded = true },
        onMapClick = { point -> onMapClick(point.latitude, point.longitude) },
    ) {
        Marker(
            state = MarkerState(position = selectedLatLng),
            title = stringResource(R.string.geofence_map_picker_title),
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
        )
    }
}

@Composable
fun MapPickerSheetContent(
    state: GeofenceMapPickerState,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onSearchResultClicked: (GeofenceSearchResult) -> Unit,
    onConfirmClicked: () -> Unit,
) {
    AutomationCardColumn {
        state.selectedAddress?.let { address ->
            Text(
                text = stringResource(R.string.geofence_selected_address, address),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(R.string.geofence_map_picker_sheet_title),
            style = MaterialTheme.typography.titleMedium,
        )
        AutomationSearchField(
            value = state.addressQuery,
            onValueChange = onSearchQueryChanged,
            onSearchClick = onSearchClicked,
            label = stringResource(R.string.geofence_search_label),
            placeholder = stringResource(R.string.geofence_search_placeholder),
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.errors.isNotEmpty()) {
            ValidationCard(errors = state.errors)
        }
        state.searchResults.forEach { result ->
            GeofenceSearchResultCard(
                result = result,
                onClick = { onSearchResultClicked(result) },
            )
        }
        Button(
            onClick = onConfirmClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.geofence_action_confirm_location))
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun geofenceMapHeroModifier(): Modifier = with(LocalNavigationSharedTransitionScope.current) {
    Modifier.sharedElement(
        sharedContentState = rememberSharedContentState(GEOFENCE_MAP_HERO_KEY),
        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
    )
}

private const val GEOFENCE_MAP_HERO_KEY = "geofence_map_hero"
private val PREVIEW_MAP_HEIGHT = 320.dp
private const val PREVIEW_MAP_ZOOM = 14f
private const val PICKER_MAP_ZOOM = 16f
private const val GEOFENCE_STROKE_WIDTH = 3f
private val GEOFENCE_FILL_COLOR = Color(0x332196F3)
private val GEOFENCE_STROKE_COLOR = Color(0xFF1976D2)
