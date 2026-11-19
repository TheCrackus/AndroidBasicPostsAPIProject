package com.example.androidbasicpostsapiproject.core.ui.state

import com.example.androidbasicpostsapiproject.core.constants.BASE_EMPTY_STRING

enum class BaseErrorType {
    GENERIC
}

data class BaseError(
    val errorTitle: String = BASE_EMPTY_STRING,
    val errorBody: String = BASE_EMPTY_STRING,
    val type: BaseErrorType = BaseErrorType.GENERIC
)
