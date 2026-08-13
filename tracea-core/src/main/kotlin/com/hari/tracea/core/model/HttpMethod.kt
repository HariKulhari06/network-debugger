package com.hari.tracea.core.model

import java.util.Locale

/**
 * HTTP methods.
 */
enum class HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, UNKNOWN;

    companion object {
        /**
         * Creates an HttpMethod from string.
         */
        fun from(method: String): HttpMethod {
            return try {
                valueOf(method.uppercase(Locale.ROOT))
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
