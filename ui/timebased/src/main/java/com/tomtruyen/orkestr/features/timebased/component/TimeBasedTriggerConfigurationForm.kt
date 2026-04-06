package com.tomtruyen.orkestr.features.timebased.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.model.Weekday
import com.tomtruyen.automation.features.triggers.config.TimeBasedTriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.ui.timebased.R
import com.tomtruyen.automation.R as AutomationR

@Composable
fun TimeBasedTriggerConfigurationForm(config: TimeBasedTriggerConfig, onFieldChanged: (String, String) -> Unit) {
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.automation_time_picker_label),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.automation_time_picker_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePickerDialog = true },
            ) {
                AutomationCardColumn {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(R.string.automation_time_picker_field_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = formatTime(config.hour, config.minute),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.automation_time_days_label),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.automation_time_days_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Weekday.entries.forEach { day ->
                    val selected = day in config.days
                    FilterChip(
                        selected = selected,
                        onClick = { onFieldChanged("day_${day.name.lowercase()}", (!selected).toString()) },
                        label = {
                            Text(text = weekdayLabel(day))
                        },
                    )
                }
            }
        }
    }

    if (showTimePickerDialog) {
        AutomationTimePickerDialog(
            hour = config.hour,
            minute = config.minute,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { hour, minute ->
                onFieldChanged("hour", hour.toString())
                onFieldChanged("minute", minute.toString())
                showTimePickerDialog = false
            },
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)

@Composable
private fun weekdayLabel(day: Weekday): String = stringResource(
    when (day) {
        Weekday.MONDAY -> AutomationR.string.automation_definition_weekday_monday
        Weekday.TUESDAY -> AutomationR.string.automation_definition_weekday_tuesday
        Weekday.WEDNESDAY -> AutomationR.string.automation_definition_weekday_wednesday
        Weekday.THURSDAY -> AutomationR.string.automation_definition_weekday_thursday
        Weekday.FRIDAY -> AutomationR.string.automation_definition_weekday_friday
        Weekday.SATURDAY -> AutomationR.string.automation_definition_weekday_saturday
        Weekday.SUNDAY -> AutomationR.string.automation_definition_weekday_sunday
    },
)
