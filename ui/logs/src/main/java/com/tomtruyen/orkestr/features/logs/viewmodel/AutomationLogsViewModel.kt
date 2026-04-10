package com.tomtruyen.orkestr.features.logs.viewmodel

import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationLog
import com.tomtruyen.automation.data.repository.AutomationLogRepository
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.features.logs.state.AutomationLogsAction
import com.tomtruyen.orkestr.features.logs.state.AutomationLogsUiState
import com.tomtruyen.orkestr.features.logs.state.reduceLogs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AutomationLogsViewModel(
    private val repository: AutomationLogRepository,
) : BaseViewModel<AutomationLogsUiState, Nothing, AutomationLogsAction>(
    initialState = AutomationLogsUiState(),
) {
    private var allLogs: List<AutomationLog> = emptyList()

    init {
        repository.observeLogs()
            .onEach { logs ->
                allLogs = logs
                updateState { state ->
                    state.copy(
                        logs = reduceLogs(
                            logs = logs,
                            query = state.query,
                            sortOption = state.sortOption,
                        ),
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun onAction(action: AutomationLogsAction) {
        when (action) {
            is AutomationLogsAction.SearchQueryChanged -> updateState { state ->
                state.copy(
                    query = action.query,
                    logs = reduceLogs(
                        logs = allLogs,
                        query = action.query,
                        sortOption = state.sortOption,
                    ),
                )
            }

            is AutomationLogsAction.SortOptionChanged -> updateState { state ->
                state.copy(
                    sortOption = action.sortOption,
                    logs = reduceLogs(
                        logs = allLogs,
                        query = state.query,
                        sortOption = action.sortOption,
                    ),
                )
            }
        }
    }
}
