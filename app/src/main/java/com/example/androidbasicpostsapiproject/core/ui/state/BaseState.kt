package com.example.androidbasicpostsapiproject.core.ui.state

/**
 * Common UI overlay state shared by every screen in the application.
 *
 * @property isLoading `true` while an asynchronous operation is in progress and a loading indicator
 * should be displayed.
 * @property error Non-null [BaseError] when a recoverable error dialog should be shown on screen.
 */
data class BaseData(
    val isLoading: Boolean = false,
    val error: BaseError? = null
) {
    /** Convenience flag indicating whether [error] is currently present. */
    val hasError: Boolean get() = error != null
}

/**
 * Base contract implemented by all screen UI state models managed by [BaseViewModel].
 *
 * @property baseData Common loading and error metadata rendered by
 * [com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay].
 */
interface BaseState {
    val baseData: BaseData
}