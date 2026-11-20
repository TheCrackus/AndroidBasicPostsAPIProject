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

/**
 * Generic Unidirectional Data Flow (UDF / MVI) base [ViewModel] standardizing state management,
 * user action handling, and one-off side effects across all feature screens.
 *
 * @param S Immutable UI state type implementing [BaseState].
 * @param E User action / intent type implementing [BaseEvent].
 * @param EF One-off side effect type implementing [BaseEffect].
 * @param initialState Initial state exposed by [uiState] before any actions are processed.
 * @param eventCapacity Capacity of the underlying [Channel] backing [uiEffects] (defaults to
 * [Channel.BUFFERED] so effects emitted during configuration changes are preserved).
 */
abstract class BaseViewModel<S : BaseState, E : BaseEvent, EF : BaseEffect>(
    initialState: S,
    eventCapacity: Int = Channel.BUFFERED
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = initialState
    )

    /** Observable stream of immutable screen state collected by the UI with lifecycle awareness. */
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffects = Channel<EF>(capacity = eventCapacity)

    /**
     * Single-consumer stream of one-off side effects (e.g. navigation) collected via
     * [com.example.androidbasicpostsapiproject.core.ui.screen.ObserveAsEvents].
     */
    val uiEffects: Flow<EF> = _uiEffects.receiveAsFlow()

    /**
     * Atomically reduces the current [uiState] into a new immutable snapshot.
     *
     * @param reducer Pure transformation invoked on the current state to produce the next state.
     */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { current ->
            current.reducer()
        }
    }

    /**
     * Single entry point for all UI actions dispatched from the screen.
     *
     * @param event Action triggered by the user or screen lifecycle.
     */
    abstract fun onAction(event: E)

    /**
     * Emits a one-off [effect] into [uiEffects] within [viewModelScope].
     *
     * @param effect Side effect to deliver once to the active UI observer.
     */
    protected fun sendEffect(effect: EF) {
        viewModelScope.launch {
            _uiEffects.send(
                element = effect
            )
        }
    }
}
