package com.tomtruyen.orkestr.features.geofence.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.model.AutomationGeofence
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.features.geofence.state.GeofenceSearchResult
import java.util.Locale

@Composable
fun GeofenceSelectionCard(geofence: AutomationGeofence, selected: Boolean, onClick: () -> Unit) {
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
                text = String.format(Locale.US, "%.0f m", geofence.radiusMeters),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun GeofenceSearchResultCard(result: GeofenceSearchResult, onClick: () -> Unit) {
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

private fun formatCoordinates(latitude: Double, longitude: Double): String =
    String.format(Locale.US, "%.6f, %.6f", latitude, longitude)

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun GeofenceSelectionListComposePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GeofenceSelectionCard(
                    geofence = AutomationGeofence(
                        id = "home",
                        name = "Home",
                        latitude = 51.219448,
                        longitude = 4.402464,
                        radiusMeters = 150f,
                        address = "Antwerp, Belgium",
                    ),
                    selected = true,
                    onClick = {},
                )
                GeofenceSelectionCard(
                    geofence = AutomationGeofence(
                        id = "office",
                        name = "Office",
                        latitude = 50.850346,
                        longitude = 4.351721,
                        radiusMeters = 250f,
                        address = "Brussels, Belgium",
                    ),
                    selected = false,
                    onClick = {},
                )
            }
        }
    }
}
