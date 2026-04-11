package com.tomtruyen.orkestr.features.logs.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.core.AutomationLogSeverity
import com.tomtruyen.orkestr.common.component.AutomationCardColumn
import com.tomtruyen.orkestr.common.component.AutomationSearchField
import com.tomtruyen.orkestr.common.component.AutomationSectionHeader
import com.tomtruyen.orkestr.common.component.EmptyStateCard
import com.tomtruyen.orkestr.features.logs.state.AutomationLogsAction
import com.tomtruyen.orkestr.features.logs.state.AutomationLogsUiState
import com.tomtruyen.orkestr.features.logs.state.LogSortOption
import com.tomtruyen.orkestr.features.logs.viewmodel.AutomationLogsViewModel
import com.tomtruyen.orkestr.ui.logs.R
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AutomationLogsScreen(modifier: Modifier = Modifier, viewModel: AutomationLogsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val logs = viewModel.logs.collectAsLazyPagingItems()
    AutomationLogsScreenContent(
        uiState = uiState,
        logs = logs,
        onQueryChanged = { viewModel.onAction(AutomationLogsAction.SearchQueryChanged(it)) },
        onSortOptionChanged = { viewModel.onAction(AutomationLogsAction.SortOptionChanged(it)) },
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AutomationLogsScreenContent(
    uiState: AutomationLogsUiState,
    logs: LazyPagingItems<AutomationLog>,
    onQueryChanged: (String) -> Unit,
    onSortOptionChanged: (LogSortOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    LogsScreenScaffold(
        uiState = uiState,
        onQueryChanged = onQueryChanged,
        onSortOptionChanged = onSortOptionChanged,
        modifier = modifier,
    ) {
        if (logs.shouldShowEmptyState()) {
            emptyLogsCard(uiState)
        } else if (logs.itemCount > 0) {
            outlinedLogsCard(logs = logs)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LogsScreenScaffold(
    uiState: AutomationLogsUiState,
    onQueryChanged: (String) -> Unit,
    onSortOptionChanged: (LogSortOption) -> Unit,
    modifier: Modifier = Modifier,
    logContent: LazyListScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.logs_title))
                },
            )
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
                LogsFilterCard(
                    query = uiState.query,
                    sortOption = uiState.sortOption,
                    onQueryChanged = onQueryChanged,
                    onSortOptionChanged = onSortOptionChanged,
                )
            }
            logContent()
        }
    }
}

private fun LazyListScope.emptyLogsCard(uiState: AutomationLogsUiState) {
    item {
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            AutomationCardColumn {
                EmptyStateCard(
                    title = stringResource(R.string.logs_empty_title),
                    description = stringResource(
                        if (uiState.query.isBlank()) {
                            R.string.logs_empty_description
                        } else {
                            R.string.logs_empty_filtered_description
                        },
                    ),
                )
            }
        }
    }
}

private fun LazyListScope.outlinedLogsCard(logs: LazyPagingItems<AutomationLog>) {
    item {
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            LogsCardHeader(loadedLogCount = logs.itemCount)
        }
    }

    items(
        count = logs.itemCount,
        key = logs.itemKey { log -> log.id },
    ) { index ->
        logs[index]?.let { log ->
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                AutomationLogRow(log = log)
            }
        }
    }

    if (logs.shouldShowLoadingMoreButton()) {
        item {
            OutlinedButton(
                onClick = {
                    logs.retry()
                    logs[logs.itemCount - 1]
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.logs_load_more))
            }
        }
    }
}

private fun LazyPagingItems<AutomationLog>.shouldShowEmptyState(): Boolean =
    itemCount == 0 && loadState.refresh is LoadState.NotLoading

private fun LazyPagingItems<AutomationLog>.shouldShowLoadingMoreButton(): Boolean = itemCount > 0 &&
    loadState.append !is LoadState.Loading &&
    loadState.append.endOfPaginationReached.not()

private fun LazyListScope.outlinedPreviewLogsCard(logs: List<AutomationLog>) {
    item {
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            LogsCardHeader(loadedLogCount = logs.size)
        }
    }

    items(
        items = logs,
        key = { log -> log.id },
    ) { log ->
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            AutomationLogRow(log = log)
        }
    }
}

@Composable
private fun LogsCardHeader(loadedLogCount: Int) {
    AutomationCardColumn {
        AutomationSectionHeader(
            title = stringResource(R.string.logs_results_title),
            description = stringResource(R.string.logs_results_description, loadedLogCount),
        )
    }
}

@Composable
private fun LogsFilterCard(
    query: String,
    sortOption: LogSortOption,
    onQueryChanged: (String) -> Unit,
    onSortOptionChanged: (LogSortOption) -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        AutomationCardColumn {
            AutomationSectionHeader(
                title = stringResource(R.string.logs_filters_title),
                description = stringResource(R.string.logs_filters_description),
            )
            AutomationSearchField(
                value = query,
                onValueChange = onQueryChanged,
                onSearchClick = { onQueryChanged(query) },
                label = stringResource(R.string.logs_search_label),
                placeholder = stringResource(R.string.logs_search_placeholder),
                modifier = Modifier.fillMaxWidth(),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.logs_sort_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LogSortOption.entries.forEach { option ->
                        SortOptionChip(
                            sortOption = option,
                            selected = sortOption == option,
                            onClick = { onSortOptionChanged(option) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SortOptionChip(
    sortOption: LogSortOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        tonalElevation = if (selected) 1.dp else 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(sortOption.labelRes()),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AutomationLogRow(log: AutomationLog, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SeverityBadge(severity = log.severity)
            Text(
                text = formatTimestamp(log.timestampEpochMillis),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = log.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        log.stackTrace?.takeIf(String::isNotBlank)?.let { stackTrace ->
            Spacer(modifier = Modifier.fillMaxWidth())
            Text(
                text = stackTrace,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun SeverityBadge(severity: AutomationLogSeverity) {
    val (label, containerColor, contentColor) = when (severity) {
        AutomationLogSeverity.DEBUG -> Triple(
            stringResource(R.string.logs_severity_debug),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AutomationLogSeverity.INFO -> Triple(
            stringResource(R.string.logs_severity_info),
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
        )

        AutomationLogSeverity.WARNING -> Triple(
            stringResource(R.string.logs_severity_warning),
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )

        AutomationLogSeverity.ERROR -> Triple(
            stringResource(R.string.logs_severity_error),
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

private fun formatTimestamp(timestampEpochMillis: Long): String = DateTimeFormatter
    .ofPattern("MMM d, HH:mm:ss")
    .format(
        Instant.ofEpochMilli(timestampEpochMillis).atZone(ZoneId.systemDefault()),
    )

private fun LogSortOption.labelRes(): Int = when (this) {
    LogSortOption.NEWEST_FIRST -> R.string.logs_sort_newest
    LogSortOption.OLDEST_FIRST -> R.string.logs_sort_oldest
    LogSortOption.SEVERITY -> R.string.logs_sort_severity
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
internal fun AutomationLogsScreenComposePreview() {
    MaterialTheme {
        val logs = listOf(
            AutomationLog(
                id = 1,
                timestampEpochMillis = 1_712_789_632_000,
                severity = AutomationLogSeverity.INFO,
                message = "Received geofence transition ENTER for home",
            ),
            AutomationLog(
                id = 2,
                timestampEpochMillis = 1_712_789_631_000,
                severity = AutomationLogSeverity.WARNING,
                message = "Skipped geofence registration because location permissions are missing",
            ),
            AutomationLog(
                id = 3,
                timestampEpochMillis = 1_712_789_630_000,
                severity = AutomationLogSeverity.ERROR,
                message = "Action SHOW_NOTIFICATION failed during parallel execution",
                stackTrace = "java.lang.IllegalStateException: Channel missing\n" +
                    "    at sample.Class.method(Class.kt:12)",
            ),
        )
        LogsScreenScaffold(
            uiState = AutomationLogsUiState(
                query = "",
                sortOption = LogSortOption.NEWEST_FIRST,
            ),
            onQueryChanged = {},
            onSortOptionChanged = {},
        ) {
            outlinedPreviewLogsCard(logs = logs)
        }
    }
}
