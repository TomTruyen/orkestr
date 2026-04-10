package com.tomtruyen.orkestr.features.logs.state

import com.tomtruyen.automation.core.AutomationLog

data class AutomationLogsUiState(
    val query: String = "",
    val sortOption: LogSortOption = LogSortOption.NEWEST_FIRST,
    val logs: List<AutomationLog> = emptyList(),
)
