package com.tomtruyen.orkestr.features.automation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.features.actions.ActionExecutionMode
import com.tomtruyen.orkestr.ui.automation.R

@Composable
fun ActionExecutionModeSelector(
    selectedMode: ActionExecutionMode,
    onModeSelected: (ActionExecutionMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.automation_actions_execution_mode_label),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.automation_actions_execution_mode_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ActionExecutionMode.entries.forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { onModeSelected(mode) },
                        label = { Text(text = stringResource(mode.labelRes())) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun ActionExecutionModeSelectorComposePreview() {
    MaterialTheme {
        Surface {
            ActionExecutionModeSelector(
                selectedMode = ActionExecutionMode.PARALLEL,
                onModeSelected = {},
            )
        }
    }
}

private fun ActionExecutionMode.labelRes(): Int = when (this) {
    ActionExecutionMode.PARALLEL -> R.string.automation_actions_execution_mode_parallel
    ActionExecutionMode.SEQUENTIAL -> R.string.automation_actions_execution_mode_sequential
}
