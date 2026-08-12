package com.hari.networkdebugger.storage.mapper

import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.ErrorType
import com.hari.networkdebugger.core.model.HttpMethod
import com.hari.networkdebugger.core.model.NetworkError
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.model.NetworkEventState
import com.hari.networkdebugger.core.model.NetworkSource
import com.hari.networkdebugger.core.model.NetworkTiming
import com.hari.networkdebugger.storage.db.NetworkEventEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object EntityMapper {
    private val json = Json { ignoreUnknownKeys = true }

    fun NetworkEvent.toEntity(requestBodyRef: String?, responseBodyRef: String?): NetworkEventEntity {
        return NetworkEventEntity(
            id = id,
            timestamp = timestamp,
            method = method.name,
            url = url,
            host = host,
            path = path,
            scheme = scheme,
            statusCode = statusCode,
            statusMessage = statusMessage,
            duration = timing.totalMs,
            requestSize = requestSize,
            responseSize = responseSize,
            source = source.name,
            state = state.name,
            errorType = error?.type?.name,
            errorMessage = error?.message,
            errorClassName = error?.throwableClassName,
            requestHeadersJson = json.encodeToString(requestHeaders),
            responseHeadersJson = json.encodeToString(responseHeaders),
            requestContentType = requestContentType,
            responseContentType = responseContentType,
            queryParametersJson = json.encodeToString(queryParameters),
            protocol = protocol,
            port = port,
            startTimestamp = timing.startTimestamp,
            endTimestamp = timing.endTimestamp,
            dnsMs = timing.dnsMs,
            connectMs = timing.connectMs,
            tlsMs = timing.tlsMs,
            waitingMs = timing.waitingMs,
            downloadMs = timing.downloadMs,
            requestBodyRef = requestBodyRef,
            responseBodyRef = responseBodyRef,
            requestBodyType = requestBody?.contentType?.name,
            responseBodyType = responseBody?.contentType?.name,
            requestBodySize = requestBody?.size,
            responseBodySize = responseBody?.size,
            sessionId = sessionId,
            sessionName = sessionName
        )
    }

    fun NetworkEventEntity.toDomain(requestBodyData: BodyData?, responseBodyData: BodyData?): NetworkEvent {
        return NetworkEvent(
            id = id,
            timestamp = timestamp,
            method = runCatching { HttpMethod.valueOf(method) }.getOrDefault(HttpMethod.GET),
            url = url,
            host = host,
            path = path,
            scheme = scheme,
            statusCode = statusCode,
            statusMessage = statusMessage,
            requestSize = requestSize,
            responseSize = responseSize,
            source = runCatching { NetworkSource.valueOf(source) }.getOrDefault(NetworkSource.OKHTTP),
            state = runCatching { NetworkEventState.valueOf(state) }.getOrDefault(NetworkEventState.COMPLETED),
            error = if (errorType != null || errorMessage != null) {
                NetworkError(
                    type = errorType?.let { runCatching { ErrorType.valueOf(it) }.getOrNull() } ?: ErrorType.UNKNOWN,
                    message = errorMessage,
                    throwableClassName = errorClassName
                )
            } else null,
            requestHeaders = runCatching { json.decodeFromString<Map<String, List<String>>>(requestHeadersJson) }.getOrDefault(emptyMap()),
            responseHeaders = runCatching { json.decodeFromString<Map<String, List<String>>>(responseHeadersJson) }.getOrDefault(emptyMap()),
            requestContentType = requestContentType,
            responseContentType = responseContentType,
            queryParameters = runCatching { json.decodeFromString<Map<String, List<String>>>(queryParametersJson) }.getOrDefault(emptyMap()),
            protocol = protocol,
            port = port,
            timing = NetworkTiming(
                startTimestamp = if (startTimestamp > 0L) startTimestamp else timestamp,
                endTimestamp = endTimestamp,
                dnsMs = dnsMs,
                connectMs = connectMs,
                tlsMs = tlsMs,
                waitingMs = waitingMs,
                downloadMs = downloadMs
            ),
            requestBody = requestBodyData,
            responseBody = responseBodyData,
            sessionId = sessionId,
            sessionName = sessionName
        )
    }
}
