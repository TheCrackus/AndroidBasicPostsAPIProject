package com.example.androidbasicpostsapiproject.core.network

import kotlinx.serialization.Serializable

/**
 * Represents the lifecycle state and outcome of an asynchronous network operation.
 *
 * @param T Type of the payload returned on [Success].
 */
sealed class NetworkResult<out T> {
    /**
     * Operation completed successfully with a non-null [data] payload.
     *
     * @property data Parsed response body or mapped domain value.
     */
    data class Success<out T>(val data: T) : NetworkResult<T>()

    /**
     * Operation failed due to an HTTP error response, empty body, connectivity issue, or
     * unexpected exception.
     *
     * @property code Optional HTTP status code when the server returned a response.
     * @property errorTitle Human-readable error summary intended for UI display.
     * @property errorBody Optional raw error payload returned by the server for debugging/details.
     */
    data class Error(
        val code: Int? = null,
        val errorTitle: String? = null,
        val errorBody: String? = null
    ) : NetworkResult<Nothing>()

    /**
     * Operation is currently in flight. Emitted by repositories before executing the remote call.
     */
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Generic error body schema used to extract readable error messages from non-2xx JSON responses
 * returned by the Node.js backend.
 *
 * @property message Primary error message returned by the API, if present.
 * @property error Fallback error field returned by some endpoints, if present.
 * @property statusCode Optional status code included inside the JSON error payload.
 */
@Serializable
data class GenericApiErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val statusCode: Int? = null,
)
