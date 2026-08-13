package com.hari.tracea.core.model

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import javax.net.ssl.SSLException

/**
 * Represents a network error.
 */
data class NetworkError(
    val type: ErrorType,
    val message: String?,
    val throwableClassName: String? = null
) {
    companion object {
        fun fromThrowable(throwable: Throwable): NetworkError {
            val type = when (throwable) {
                is SocketTimeoutException -> ErrorType.TIMEOUT
                is UnknownHostException -> ErrorType.DNS_FAILURE
                is ConnectException -> ErrorType.CONNECTION_FAILURE
                is SSLException -> ErrorType.TLS_ERROR
                is CancellationException -> ErrorType.CANCELLED
                is IOException -> ErrorType.IO_ERROR
                else -> ErrorType.UNKNOWN
            }
            return NetworkError(
                type = type,
                message = throwable.message,
                throwableClassName = throwable::class.qualifiedName
            )
        }

        fun fromStatusCode(code: Int): NetworkError? {
            return when (code) {
                in 400..499 -> NetworkError(ErrorType.HTTP_CLIENT_ERROR, "HTTP Client Error: $code")
                in 500..599 -> NetworkError(ErrorType.HTTP_SERVER_ERROR, "HTTP Server Error: $code")
                else -> null
            }
        }
    }
}

/**
 * Types of network errors.
 */
enum class ErrorType {
    HTTP_CLIENT_ERROR, HTTP_SERVER_ERROR,
    TIMEOUT, DNS_FAILURE, CONNECTION_FAILURE,
    TLS_ERROR, CANCELLED, IO_ERROR,
    MALFORMED_RESPONSE, UNKNOWN
}
