package com.hari.tracea.core.model

import com.hari.tracea.core.util.mapThrowableToNetworkError

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
            return mapThrowableToNetworkError(throwable)
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
