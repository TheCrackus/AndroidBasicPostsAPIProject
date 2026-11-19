package com.example.androidbasicpostsapiproject.core.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : BaseState, E : BaseEvent, EF : BaseEffect>(
    initialState: S,
    eventCapacity: Int = Channel.BUFFERED
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = initialState
    )
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffects = Channel<EF>(capacity = eventCapacity)

    val uiEffects: Flow<EF> = _uiEffects.receiveAsFlow()

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { current ->
            current.reducer()
        }
    }

    abstract fun onAction(event: E)

    protected fun sendEffect(effect: EF) {
        viewModelScope.launch {
            _uiEffects.send(
                element = effect
            )
        }
    }
}
