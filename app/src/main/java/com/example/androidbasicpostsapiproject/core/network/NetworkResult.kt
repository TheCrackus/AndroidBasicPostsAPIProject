package com.example.androidbasicpostsapiproject.core.network

import kotlinx.serialization.Serializable

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error(
        val code: Int? = null,
        val errorTitle: String? = null,
        val errorBody: String? = null
    ) : NetworkResult<Nothing>()

    data object Loading : NetworkResult<Nothing>()
}

@Serializable
data class GenericApiErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val statusCode: Int? = null,
)
