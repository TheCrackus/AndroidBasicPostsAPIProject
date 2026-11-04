package com.example.androidbasicpostsapiconsumerproject.core.network

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

data class GenericApiErrorResponse(
    val message: String?,
    val error: String?,
    val statusCode: Int?
)

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
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
                        errorTitle = "Empty response"
                    )
                }
            } else {
                val rawError = response.errorBody()?.string()

                val parsedMessage = try {
                    rawError?.let {
                        val parsed = Gson().fromJson(
                            it,
                            GenericApiErrorResponse::class.java
                        )

                        parsed.message ?: parsed.error
                    }
                } catch (_: Exception) {
                    null
                }

                NetworkResult.Error(
                    code = response.code(),
                    errorTitle = parsedMessage ?: response.message()
                        .ifBlank { "Error HTTP ${response.code()}" },
                    errorBody = rawError
                )
            }
        } catch (e: SocketTimeoutException) {
            NetworkResult.Error(
                errorTitle = "Tiempo de espera agotado al conectar con el servidor Node.js."
            )
        } catch (e: IOException) {
            NetworkResult.Error(
                errorTitle = "Sin conexión con el servidor: ${e.localizedMessage ?: "Verifica que Node.js esté corriendo"}"
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                errorTitle = "Error inesperado: ${e.localizedMessage ?: "Excepción no identificada"}"
            )
        }
    }
}
