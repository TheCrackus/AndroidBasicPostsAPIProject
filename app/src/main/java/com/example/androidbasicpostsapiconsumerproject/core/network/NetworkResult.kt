package com.example.androidbasicpostsapiconsumerproject.core.network

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error(
        val code: Int? = null,
        val errorTitle: String? = null,
        val errorBody: String? = null
    ) : NetworkResult<Nothing>()

    data object Loading : NetworkResult<Nothing>()
}
