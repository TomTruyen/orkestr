package com.tomtruyen.orkestr.features.automation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tomtruyen.automation.features.actions.ActionType
import com.tomtruyen.automation.features.actions.config.LaunchApplicationActionConfig
import com.tomtruyen.automation.features.triggers.TriggerType
import com.tomtruyen.automation.features.triggers.config.ApplicationLifecycleTriggerConfig
import com.tomtruyen.automation.features.triggers.config.NotificationReceivedTriggerConfig
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationDefinitionHeaderCard
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.features.automation.service.InstalledAppOption
import com.tomtruyen.orkestr.features.automation.service.InstalledAppService
import com.tomtruyen.orkestr.features.automation.viewmodel.AutomationRuleEditorViewModel
import com.tomtruyen.orkestr.ui.automation.R
import org.koin.compose.koinInject

@Composable
fun AutomationNotificationTriggerAppSelectionScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
    installedAppService: InstalledAppService = koinInject(),
) {
    AutomationInstalledAppPickerScreen(
        viewModel = viewModel,
        modifier = modifier,
        installedAppService = installedAppService,
    )
}

@Composable
fun AutomationApplicationTriggerAppSelectionScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
    installedAppService: InstalledAppService = koinInject(),
) {
    AutomationInstalledAppPickerScreen(
        viewModel = viewModel,
        modifier = modifier,
        installedAppService = installedAppService,
    )
}

@Composable
fun AutomationLaunchApplicationActionAppSelectionScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
    installedAppService: InstalledAppService = koinInject(),
) {
    AutomationInstalledAppPickerScreen(
        viewModel = viewModel,
        modifier = modifier,
        installedAppService = installedAppService,
    )
}

@Composable
private fun AutomationInstalledAppPickerScreen(
    viewModel: AutomationRuleEditorViewModel,
    modifier: Modifier = Modifier,
    installedAppService: InstalledAppService,
) {
    val uiState by viewModel.uiState.collectAsState()
    val pickerState = uiState.pickerState ?: return
    val definition = viewModel.selectedDefinitionItem() ?: return
    val selectedTypeKey = uiState.pickerState?.selectedTypeKey
    val selectedPackageName = when (selectedTypeKey) {
        TriggerType.APPLICATION_LIFECYCLE.name ->
            viewModel.currentDraftConfigOrDefault(ApplicationLifecycleTriggerConfig::class).packageName

        ActionType.LAUNCH_APPLICATION.name ->
            viewModel.currentDraftConfigOrDefault(LaunchApplicationActionConfig::class).packageName

        else ->
            viewModel.currentDraftConfigOrDefault(NotificationReceivedTriggerConfig::class).packageName
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
            AutomationDefinitionHeaderCard(
                title = stringResource(definition.titleRes),
                description = stringResource(definition.descriptionRes),
                isBeta = definition.isBeta,
                requiredMinSdk = definition.requiredMinSdk,
                chooseDifferentLabel = stringResource(
                    R.string.automation_action_choose_different,
                    stringResource(pickerState.section.singularTitleRes),
                ),
                onChooseDifferent = viewModel::chooseDifferentDefinition,
            )
        }

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
            InstalledAppCard(
                app = app,
                isSelected = selectedPackageName == app.packageName,
                onClick = {
                    viewModel.applySelectedApp(
                        selectedTypeKey = selectedTypeKey,
                        packageName = app.packageName,
                        appLabel = app.label,
                    )
                },
            )
        }
    }
}

@Composable
private fun InstalledAppCard(app: InstalledAppOption, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            if (isSelected) {
                Text(
                    text = stringResource(R.string.automation_action_use_selected_app),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
