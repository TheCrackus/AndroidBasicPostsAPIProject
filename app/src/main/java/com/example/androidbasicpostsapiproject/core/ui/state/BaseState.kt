package com.example.androidbasicpostsapiproject.core.ui.state

/**
 * Common UI overlay state shared by every screen in the application.
 *
 * @property isLoading `true` while an asynchronous operation is in progress and a loading indicator
 * should be displayed.
 * @property error Non-null [BaseError] when a recoverable error dialog should be shown on screen.
 * @property dialog Non-null [BaseDialog] when a generic notification dialog should be shown.
 */
data class BaseData(
    val isLoading: Boolean = false,
    val error: BaseError? = null,
    val dialog: BaseDialog? = null
) {
    /** Convenience flag indicating whether [error] is currently present. */
    val hasError: Boolean get() = error != null

    /** Convenience flag indicating whether a notification [dialog] is currently present. */
    val hasDialog: Boolean get() = dialog != null
}

/**
 * Base contract implemented by all screen UI state models managed by [BaseViewModel].
 *
 * @property baseData Common loading, error, and notification dialog metadata rendered by
 * [com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay].
 */
interface BaseState {
    val baseData: BaseData
}