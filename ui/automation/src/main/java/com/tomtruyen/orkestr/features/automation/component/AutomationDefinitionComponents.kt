package com.tomtruyen.orkestr.features.automation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.config.AutomationConfig
import com.tomtruyen.automation.core.definition.AutomationFieldDefinition
import com.tomtruyen.automation.core.definition.AutomationFieldType
import com.tomtruyen.automation.core.utils.ComparisonOperator
import com.tomtruyen.automation.features.actions.config.LogMessageActionConfig
import com.tomtruyen.automation.features.actions.definition.LogMessageActionDefinition
import com.tomtruyen.automation.features.constraints.config.BatteryLevelConstraintConfig
import com.tomtruyen.automation.features.constraints.definition.BatteryLevelConstraintDefinition

@Composable
fun DefinitionFieldPreview(fields: List<AutomationFieldDefinition>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        fields.forEach { field ->
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text(stringResource(field.labelRes)) },
                enabled = false,
            )
        }
    }
}

@Composable
fun AutomationFieldForm(
    fields: List<AutomationFieldDefinition>,
    config: AutomationConfig<*>?,
    onFieldChanged: (String, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        fields.forEach { field ->
            when (field.type) {
                AutomationFieldType.BOOLEAN -> {
                    val selected = field.readValue(config).ifBlank { field.defaultValue } == "true"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(text = stringResource(field.labelRes), style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = stringResource(field.descriptionRes),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = selected,
                            onCheckedChange = { onFieldChanged(field.id, it.toString()) },
                        )
                    }
                }

                AutomationFieldType.SINGLE_CHOICE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = stringResource(field.labelRes), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = stringResource(field.descriptionRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            field.options.forEach { option ->
                                FilterChip(
                                    selected = field.readValue(config).ifBlank { field.defaultValue } == option.value,
                                    onClick = { onFieldChanged(field.id, option.value) },
                                    label = { Text(stringResource(option.labelRes)) },
                                )
                            }
                        }
                    }
                }

                else -> {
                    val value = field.readValue(config)
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            if (field.type != AutomationFieldType.NUMBER || it.all(Char::isDigit)) {
                                onFieldChanged(field.id, it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(field.labelRes)) },
                        placeholder = {
                            Text(field.placeholderRes?.let { stringResource(it) } ?: field.defaultValue)
                        },
                        supportingText = { Text(stringResource(field.descriptionRes)) },
                        singleLine = field.type != AutomationFieldType.TEXT,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun DefinitionFieldPreviewComposePreview() {
    AutomationDefinitionPreviewSurface {
        DefinitionFieldPreview(fields = BatteryLevelConstraintDefinition.fields)
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
internal fun AutomationFieldFormComposePreview() {
    AutomationDefinitionPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AutomationFieldForm(
                fields = BatteryLevelConstraintDefinition.fields,
                config = BatteryLevelConstraintConfig(
                    operator = ComparisonOperator.LESS_THAN_OR_EQUAL,
                    value = 25,
                ),
                onFieldChanged = { _, _ -> },
            )
            AutomationFieldForm(
                fields = LogMessageActionDefinition.fields,
                config = LogMessageActionConfig(message = "Battery is low, charging mode disabled."),
                onFieldChanged = { _, _ -> },
            )
        }
    }
}

@Composable
private fun AutomationDefinitionPreviewSurface(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
