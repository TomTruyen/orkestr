package com.tomtruyen.orkestr.features.logs.state

data class AutomationLogsUiState(val query: String = "", val sortOption: LogSortOption = LogSortOption.NEWEST_FIRST)
