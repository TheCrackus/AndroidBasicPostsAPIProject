package com.example.androidbasicpostsapiproject.core.network

import com.example.androidbasicpostsapiproject.core.constants.EMPTY_RESPONSE_ERROR_TITLE
import com.example.androidbasicpostsapiproject.core.constants.ERROR_HTTP_PREFIX
import com.example.androidbasicpostsapiproject.core.constants.NO_IDENTIFIED_ERROR_MESSAGE
import com.example.androidbasicpostsapiproject.core.constants.NO_SERVER_CONNECTION_PREFIX
import com.example.androidbasicpostsapiproject.core.constants.TIME_ERROR_MESSAGE
import com.example.androidbasicpostsapiproject.core.constants.UNEXPECTED_ERROR_MESSAGE_PREFIX
import com.example.androidbasicpostsapiproject.core.constants.VERIFY_NODE_JS_IS_RUNNING_ERROR_MESSAGE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

private val defaultNetworkJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    json: Json = defaultNetworkJson,
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    NetworkResult.Success(data = body)
                } else {
                    NetworkResult.Error(
                        code = response.code(),
                        errorTitle = EMPTY_RESPONSE_ERROR_TITLE
                    )
                }
            } else {
                val rawError = response.errorBody()?.string()

                val parsedMessage = try {
                    rawError?.let {
                        val parsed = json.decodeFromString<GenericApiErrorResponse>(it)
                        parsed.message ?: parsed.error
                    }
                } catch (_: Exception) {
                    null
                }

                NetworkResult.Error(
                    code = response.code(),
                    errorTitle = parsedMessage ?: response.message()
                        .ifBlank { ERROR_HTTP_PREFIX + " ${response.code()}" },
                    errorBody = rawError
                )
            }
        } catch (_: SocketTimeoutException) {
            NetworkResult.Error(
                errorTitle = TIME_ERROR_MESSAGE
            )
        } catch (e: IOException) {
            NetworkResult.Error(
                errorTitle = NO_SERVER_CONNECTION_PREFIX + " ${e.localizedMessage ?: VERIFY_NODE_JS_IS_RUNNING_ERROR_MESSAGE}"
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            NetworkResult.Error(
                errorTitle = UNEXPECTED_ERROR_MESSAGE_PREFIX + " ${e.localizedMessage ?: NO_IDENTIFIED_ERROR_MESSAGE}"
            )
        }
    }
}
