package com.hari.tracea.core.model

/**
 * Represents a captured network event.
 */
data class NetworkEvent(
    val id: String,
    val timestamp: Long,
    val method: HttpMethod,
    val url: String,
    val scheme: String,
    val host: String,
    val port: Int?,
    val path: String,
    val queryParameters: Map<String, List<String>>,
    val protocol: String?,
    val requestHeaders: Map<String, List<String>>,
    val requestBody: BodyData?,
    val requestContentType: String?,
    val requestSize: Long,
    val statusCode: Int?,
    val statusMessage: String?,
    val responseHeaders: Map<String, List<String>>,
    val responseBody: BodyData?,
    val responseContentType: String?,
    val responseSize: Long,
    val timing: NetworkTiming,
    val error: NetworkError?,
    val source: NetworkSource,
    val state: NetworkEventState,
    val sessionId: String,
    val sessionName: String
)
