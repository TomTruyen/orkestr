package com.tomtruyen.orkestr.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseViewModel<UIState, UIEvent, UIAction>(
    initialState: UIState
): ViewModel() {
    private val vmScope = viewModelScope + Dispatchers.IO

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("BaseViewModel", "Coroutine exception", throwable)
    }

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<UIEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    protected fun launch(block: suspend () -> Unit) = vmScope.launch(exceptionHandler) {
        block()
    }

    protected fun updateState(block: (UIState) -> UIState) {
        _uiState.update(block)
    }

    protected fun triggerEvent(event: UIEvent) = _eventChannel.trySend(event)

    abstract fun onAction(action: UIAction)
}