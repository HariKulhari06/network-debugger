package com.hari.networkdebugger.kmp.model

import kotlinx.serialization.Serializable

@Serializable
enum class HttpMethodKmp {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, UNKNOWN;

    companion object {
        fun from(method: String): HttpMethodKmp {
            return entries.find { it.name.equals(method, ignoreCase = true) } ?: UNKNOWN
        }
    }
}

@Serializable
enum class NetworkSourceKmp {
    OKHTTP, URLSESSION, KTOR, MANUAL
}

@Serializable
enum class NetworkStateKmp {
    STARTED, REQUEST_CAPTURED, RESPONSE_RECEIVED, COMPLETED, FAILED, CANCELLED
}

@Serializable
data class NetworkTimingKmp(
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val dnsMs: Long? = null,
    val connectMs: Long? = null,
    val tlsMs: Long? = null,
    val waitingMs: Long? = null,
    val downloadMs: Long? = null
) {
    val totalMs: Long?
        get() = endTimestamp?.let { it - startTimestamp }
}

@Serializable
data class NetworkEventKmp(
    val id: String,
    val timestamp: Long,
    val method: HttpMethodKmp,
    val url: String,
    val scheme: String,
    val host: String,
    val port: Int? = null,
    val path: String,
    val queryParameters: Map<String, List<String>> = emptyMap(),
    val protocol: String? = null,
    val requestHeaders: Map<String, List<String>> = emptyMap(),
    val requestBodyText: String? = null,
    val requestContentType: String? = null,
    val requestSize: Long = 0L,
    val statusCode: Int? = null,
    val statusMessage: String? = null,
    val responseHeaders: Map<String, List<String>> = emptyMap(),
    val responseBodyText: String? = null,
    val responseContentType: String? = null,
    val responseSize: Long = 0L,
    val timing: NetworkTimingKmp,
    val errorMessage: String? = null,
    val source: NetworkSourceKmp,
    val state: NetworkStateKmp
)
