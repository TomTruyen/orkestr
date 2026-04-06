package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tomtruyen.automation.core.model.WifiRangeTriggerType
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.WifiSsidTriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.features.automation.service.InstalledAppOption
import com.tomtruyen.orkestr.features.automation.service.InstalledAppService
import com.tomtruyen.orkestr.features.automation.service.WifiNetworkService
import com.tomtruyen.orkestr.ui.automation.R
import org.koin.compose.koinInject

@Composable
fun AutomationNotificationTriggerAppSelectionScreen(
    viewModel: com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
    installedAppService: InstalledAppService = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTypeKey = uiState.pickerState?.selectedTypeKey
    val selectedPackageName = when (selectedTypeKey) {
        TriggerType.APPLICATION_LIFECYCLE.name -> {
            viewModel.currentApplicationLifecycleTriggerConfig().packageName
        }

        else -> viewModel.currentNotificationTriggerConfig().packageName
    }
    val apps = remember(installedAppService) { installedAppService.loadInstalledApps() }
    var query by rememberSaveable { mutableStateOf("") }

    val filteredApps = remember(apps, query) {
        apps.filter { app ->
            query.isBlank() ||
                app.label.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.automation_notification_app_search_label)) },
                placeholder = { Text(stringResource(R.string.automation_notification_app_search_placeholder)) },
                singleLine = true,
            )
        }

        if (filteredApps.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.automation_empty_apps_title),
                    description = stringResource(R.string.automation_empty_apps_description),
                )
            }
        }

        items(filteredApps, key = InstalledAppOption::packageName) { app ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (selectedTypeKey) {
                            TriggerType.APPLICATION_LIFECYCLE.name -> {
                                viewModel.applySelectedApplication(app.packageName)
                            }

                            else -> viewModel.applySelectedNotificationApp(app.packageName)
                        }
                    },
            ) {
                AutomationCardColumn {
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (selectedPackageName == app.packageName) {
                        Text(
                            text = stringResource(R.string.automation_action_use_selected_app),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AutomationWifiTriggerSelectionScreen(
    viewModel: com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
    wifiNetworkService: WifiNetworkService = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState
    val definition = viewModel.selectedDefinitionItem()
    val currentConfig = viewModel.currentWifiTriggerConfig()
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

    fun save(ssid: String) {
        viewModel.applySelectedWifiTrigger(
            WifiSsidTriggerConfig(
                ssid = ssid.trim(),
                triggerType = triggerType,
            ),
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (pickerState?.launchedFromSelection == true) {
            item {
                AutomationDefinitionHeaderCard(
                    title = definition?.let { stringResource(it.titleRes) }
                        ?: stringResource(R.string.automation_title_select_wifi_network),
                    description = definition?.let { stringResource(it.descriptionRes) }
                        ?: stringResource(R.string.automation_wifi_section_discovered_description),
                    isBeta = definition?.isBeta == true,
                    chooseDifferentLabel = stringResource(
                        R.string.automation_action_choose_different,
                        stringResource(R.string.automation_singular_trigger),
                    ),
                    onChooseDifferent = {
                        viewModel.onAction(
                            com.tomtruyen.orkestr.features.automation.state
                                .AutomationEditorAction.BackToPickerSelectionClicked,
                        )
                    },
                )
            }
        }

        item {
            AutomationPickerSectionCard {
                AutomationPickerSectionHeader(
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
            AutomationPickerSectionCard {
                AutomationPickerSectionHeader(
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
                                        save(ssid)
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
            AutomationPickerSectionCard {
                AutomationPickerSectionHeader(
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
                    supportingText = { Text(stringResource(R.string.automation_wifi_manual_description)) },
                    singleLine = true,
                )
                Button(
                    onClick = { save(manualSsid) },
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
private fun AutomationPickerSectionCard(content: @Composable () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        AutomationCardColumn {
            content()
        }
    }
}

@Composable
private fun AutomationPickerSectionHeader(title: String, description: String) {
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
