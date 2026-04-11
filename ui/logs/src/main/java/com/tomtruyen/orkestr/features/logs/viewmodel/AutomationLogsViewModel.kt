package com.tomtruyen.orkestr.features.logs.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.data.repository.AutomationLogRepository
import com.tomtruyen.automation.data.repository.AutomationLogSort
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.features.logs.state.AutomationLogsAction
import com.tomtruyen.orkestr.features.logs.state.AutomationLogsUiState
import com.tomtruyen.orkestr.features.logs.state.LogSortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class AutomationLogsViewModel(private val repository: AutomationLogRepository) :
    BaseViewModel<AutomationLogsUiState, Nothing, AutomationLogsAction>(
        initialState = AutomationLogsUiState(),
    ) {
    private val request = MutableStateFlow(LogRequest())
    val logs: Flow<PagingData<AutomationLog>> =
        request
            .flatMapLatest { request ->
                repository.observeLogs(
                    query = request.query,
                    sort = request.sortOption.toRepositorySort(),
                )
            }
            .cachedIn(viewModelScope)

    override fun onAction(action: AutomationLogsAction) {
        when (action) {
            is AutomationLogsAction.SearchQueryChanged -> {
                updateState { state ->
                    state.copy(query = action.query)
                }
                request.update { state ->
                    state.copy(query = action.query)
                }
            }

            is AutomationLogsAction.SortOptionChanged -> {
                updateState { state ->
                    state.copy(sortOption = action.sortOption)
                }
                request.update { state ->
                    state.copy(sortOption = action.sortOption)
                }
            }
        }
    }
}

private data class LogRequest(val query: String = "", val sortOption: LogSortOption = LogSortOption.NEWEST_FIRST)

private fun LogSortOption.toRepositorySort(): AutomationLogSort = when (this) {
    LogSortOption.NEWEST_FIRST -> AutomationLogSort.NEWEST_FIRST
    LogSortOption.OLDEST_FIRST -> AutomationLogSort.OLDEST_FIRST
    LogSortOption.SEVERITY -> AutomationLogSort.SEVERITY
}
