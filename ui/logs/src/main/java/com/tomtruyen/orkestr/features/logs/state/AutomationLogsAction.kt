package com.tomtruyen.orkestr.features.logs.state

sealed interface AutomationLogsAction {
    data class SearchQueryChanged(val query: String) : AutomationLogsAction

    data class SortOptionChanged(val sortOption: LogSortOption) : AutomationLogsAction
}
