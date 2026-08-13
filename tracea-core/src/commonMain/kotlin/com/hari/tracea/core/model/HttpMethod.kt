package com.hari.tracea.core.model

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
                valueOf(method.uppercase())
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
