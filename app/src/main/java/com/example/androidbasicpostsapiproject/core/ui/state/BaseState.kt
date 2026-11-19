package com.example.androidbasicpostsapiproject.core.ui.state

/**
 * isLoading: true if we should show a loading behavior
 *
 * error: not null if an error occurs and we should show
 * on screen
 */
data class BaseData(
    val isLoading: Boolean = false,
    val error: BaseError? = null
) {
    val hasError: Boolean get() = error != null
}

interface BaseState {
    val baseData: BaseData
}