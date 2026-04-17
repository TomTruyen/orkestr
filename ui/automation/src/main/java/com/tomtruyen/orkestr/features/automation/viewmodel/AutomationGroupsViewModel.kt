package com.tomtruyen.orkestr.features.automation.viewmodel

import androidx.lifecycle.viewModelScope
import com.tomtruyen.automation.core.AutomationNodeGroup
import com.tomtruyen.automation.core.AutomationNodeGroupType
import com.tomtruyen.automation.data.repository.AutomationNodeGroupRepository
import com.tomtruyen.orkestr.common.BaseViewModel
import com.tomtruyen.orkestr.features.automation.state.AutomationGroupsAction
import com.tomtruyen.orkestr.features.automation.state.AutomationGroupsEvent
import com.tomtruyen.orkestr.features.automation.state.AutomationGroupsUiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

class AutomationGroupsViewModel(private val repository: AutomationNodeGroupRepository) :
    BaseViewModel<AutomationGroupsUiState, AutomationGroupsEvent, AutomationGroupsAction>(
        initialState = AutomationGroupsUiState(),
    ) {
    init {
        repository.observeGroups()
            .onEach { groups -> updateState { it.copy(groups = groups) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AutomationGroupsAction) {
        when (action) {
            is AutomationGroupsAction.DeleteGroupClicked -> deleteGroup(action.group)
            is AutomationGroupsAction.UpdateGroupClicked -> updateGroup(action.group)
            is AutomationGroupsAction.CreateEmptyGroupClicked -> createEmptyGroup(action.type, action.name)
        }
    }

    private fun deleteGroup(group: AutomationNodeGroup) = launch {
        repository.deleteGroup(group.id)
    }

    private fun updateGroup(group: AutomationNodeGroup) = launch {
        if (group.name.isBlank()) return@launch
        repository.upsertGroup(group.copy(name = group.name.trim()))
    }

    private fun createEmptyGroup(type: AutomationNodeGroupType, name: String) = launch {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return@launch
        repository.upsertGroup(
            AutomationNodeGroup(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                type = type,
            ),
        )
    }
}
