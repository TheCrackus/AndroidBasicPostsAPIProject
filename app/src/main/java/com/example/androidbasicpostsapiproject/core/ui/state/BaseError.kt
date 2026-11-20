package com.example.androidbasicpostsapiproject.core.ui.state

import com.example.androidbasicpostsapiproject.core.constants.BASE_EMPTY_STRING

/**
 * Categorizes presentation-level errors rendered by
 * [com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay].
 */
enum class BaseErrorType {
    GENERIC
}

/**
 * UI-ready representation of an error displayed inside an error dialog.
 *
 * @property errorTitle Short summary shown as the dialog title (falls back to a localized default
 * resource if blank).
 * @property errorBody Detailed error description shown in the dialog body.
 * @property type Classification of the error for custom presentation or recovery behavior.
 */
data class BaseError(
    val errorTitle: String = BASE_EMPTY_STRING,
    val errorBody: String = BASE_EMPTY_STRING,
    val type: BaseErrorType = BaseErrorType.GENERIC
)
