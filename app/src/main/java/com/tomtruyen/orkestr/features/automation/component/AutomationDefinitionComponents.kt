package com.tomtruyen.orkestr.features.automation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.data.definition.AutomationFieldDefinition
import com.tomtruyen.automation.data.definition.AutomationFieldType

@Composable
fun DefinitionFieldPreview(fields: List<AutomationFieldDefinition>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fields.forEach { field ->
            FilterChip(selected = false, onClick = {}, label = { Text(field.label) }, enabled = false)
        }
    }
}

@Composable
fun AutomationFieldForm(
    fields: List<AutomationFieldDefinition>,
    values: Map<String, String>,
    onFieldChanged: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        fields.forEach { field ->
            when (field.type) {
                AutomationFieldType.BOOLEAN -> {
                    val selected = values[field.id].orEmpty().ifBlank { field.defaultValue } == "true"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = field.label, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = field.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = selected,
                            onCheckedChange = { onFieldChanged(field.id, it.toString()) }
                        )
                    }
                }

                AutomationFieldType.SINGLE_CHOICE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = field.label, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = field.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            field.options.forEach { option ->
                                FilterChip(
                                    selected = values[field.id].orEmpty().ifBlank { field.defaultValue } == option.value,
                                    onClick = { onFieldChanged(field.id, option.value) },
                                    label = { Text(option.label) }
                                )
                            }
                        }
                    }
                }

                else -> {
                    OutlinedTextField(
                        value = values[field.id].orEmpty(),
                        onValueChange = { onFieldChanged(field.id, it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(field.label) },
                        placeholder = { Text(field.placeholder.ifBlank { field.defaultValue }) },
                        supportingText = { Text(field.description) },
                        singleLine = field.type != AutomationFieldType.TEXT
                    )
                }
            }
        }
    }
}
