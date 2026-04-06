package com.tomtruyen.orkestr.features.wifi.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.model.WifiRangeTriggerType
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.features.wifi.service.WifiNetworkService
import com.tomtruyen.orkestr.ui.wifi.R
import org.koin.compose.koinInject

@Composable
fun WifiTriggerSelectionScreen(
    currentConfig: WifiSsidTriggerConfig,
    title: String?,
    description: String?,
    isBeta: Boolean,
    requiredMinSdk: Int?,
    chooseDifferentLabel: String?,
    onChooseDifferent: (() -> Unit)?,
    onWifiSelected: (WifiSsidTriggerConfig) -> Unit,
    modifier: Modifier = Modifier,
    wifiNetworkService: WifiNetworkService = koinInject(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var manualSsid by rememberSaveable { mutableStateOf(currentConfig.ssid) }
    var triggerType by rememberSaveable { mutableStateOf(currentConfig.triggerType) }
    var discoveredSsids by remember(wifiNetworkService) {
        mutableStateOf(wifiNetworkService.loadAvailableNetworks().discoveredSsids)
    }

    LaunchedEffect(wifiNetworkService) {
        discoveredSsids = wifiNetworkService.refreshAvailableNetworks().discoveredSsids
    }

    val allSsids = remember(discoveredSsids, manualSsid, currentConfig.ssid) {
        (discoveredSsids + listOf(currentConfig.ssid, manualSsid))
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinct()
            .sorted()
    }
    val filteredSsids = remember(allSsids, query) {
        allSsids.filter { ssid -> query.isBlank() || ssid.contains(query, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (title != null && description != null) {
            item {
                AutomationDefinitionHeaderCard(
                    title = title,
                    description = description,
                    isBeta = isBeta,
                    requiredMinSdk = requiredMinSdk,
                    chooseDifferentLabel = chooseDifferentLabel,
                    onChooseDifferent = onChooseDifferent,
                )
            }
        }

        item {
            WifiPickerSectionCard {
                WifiPickerSectionHeader(
                    title = stringResource(R.string.automation_wifi_section_trigger_type_title),
                    description = stringResource(R.string.automation_wifi_section_trigger_type_description),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = triggerType == WifiRangeTriggerType.IN_RANGE,
                        onClick = { triggerType = WifiRangeTriggerType.IN_RANGE },
                        label = {
                            Text(
                                stringResource(
                                    com.tomtruyen.automation.R.string
                                        .automation_definition_trigger_wifi_ssid_option_in_range,
                                ),
                            )
                        },
                    )
                    FilterChip(
                        selected = triggerType == WifiRangeTriggerType.OUT_OF_RANGE,
                        onClick = { triggerType = WifiRangeTriggerType.OUT_OF_RANGE },
                        label = {
                            Text(
                                stringResource(
                                    com.tomtruyen.automation.R.string
                                        .automation_definition_trigger_wifi_ssid_option_out_of_range,
                                ),
                            )
                        },
                    )
                }
            }
        }

        item {
            WifiPickerSectionCard {
                WifiPickerSectionHeader(
                    title = stringResource(R.string.automation_wifi_section_discovered_title),
                    description = stringResource(R.string.automation_wifi_section_discovered_description),
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.automation_wifi_search_label)) },
                    placeholder = { Text(stringResource(R.string.automation_wifi_search_placeholder)) },
                    singleLine = true,
                )
                if (filteredSsids.isEmpty()) {
                    EmptyStateCard(
                        title = stringResource(R.string.automation_empty_wifi_title),
                        description = stringResource(R.string.automation_empty_wifi_description),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredSsids.forEach { ssid ->
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        manualSsid = ssid
                                        onWifiSelected(
                                            WifiSsidTriggerConfig(ssid = ssid.trim(), triggerType = triggerType),
                                        )
                                    },
                            ) {
                                AutomationCardColumn {
                                    Text(
                                        text = ssid,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    if (ssid == currentConfig.ssid) {
                                        Text(
                                            text = stringResource(R.string.automation_action_use_selected_wifi),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            WifiPickerSectionCard {
                WifiPickerSectionHeader(
                    title = stringResource(R.string.automation_wifi_section_manual_title),
                    description = stringResource(R.string.automation_wifi_section_manual_description),
                )
                OutlinedTextField(
                    value = manualSsid,
                    onValueChange = { manualSsid = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.automation_wifi_manual_label)) },
                    placeholder = {
                        Text(
                            stringResource(
                                com.tomtruyen.automation.R.string
                                    .automation_definition_trigger_wifi_ssid_field_ssid_placeholder,
                            ),
                        )
                    },
                    supportingText = { Text(stringResource(R.string.automation_wifi_section_manual_description)) },
                    singleLine = true,
                )
                Button(
                    onClick = {
                        onWifiSelected(
                            WifiSsidTriggerConfig(
                                ssid = manualSsid.trim(),
                                triggerType = triggerType,
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = manualSsid.isNotBlank(),
                ) {
                    Text(stringResource(R.string.automation_action_use_selected_wifi))
                }
            }
        }
    }
}

@Composable
private fun WifiPickerSectionCard(content: @Composable () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        AutomationCardColumn {
            content()
        }
    }
}

@Composable
private fun WifiPickerSectionHeader(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
