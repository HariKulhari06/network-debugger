package com.hari.networkdebugger.okhttp

import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.model.ErrorType
import com.hari.networkdebugger.core.model.HttpMethod
import com.hari.networkdebugger.core.model.NetworkError
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.model.NetworkEventState
import com.hari.networkdebugger.core.model.NetworkSource
import com.hari.networkdebugger.core.model.NetworkTiming
import com.hari.networkdebugger.core.pipeline.NetworkEventCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.UUID

/**
 * An OkHttp Interceptor that captures network requests and responses for the Network Debugger.
 */
public class NetworkDebuggerInterceptor(
    private val collector: NetworkEventCollector,
    private val config: NetworkDebuggerConfig,
    private val timingCapture: OkHttpTimingCapture? = null
) : Interceptor {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val bodyExtractor = OkHttpBodyExtractor(config.bodyCaptureConfig)

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!config.enabled) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val eventId = UUID.randomUUID().toString()
        val startNs = System.nanoTime()
        val startMs = System.currentTimeMillis()

        // Capture initial request event
        val requestEvent = try {
            createRequestEvent(eventId, request, startMs)
        } catch (e: Exception) {
            null
        }

        requestEvent?.let { event ->
            scope.launch { collector.emit(event) }
        }

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            val endNs = System.nanoTime()
            val endMs = System.currentTimeMillis()
            val durationMs = (endNs - startNs) / 1_000_000L
            val errorEvent = createErrorEvent(eventId, request, requestEvent, e, startMs, endMs, durationMs)
            scope.launch { collector.emit(errorEvent) }
            throw e
        }

        val endNs = System.nanoTime()
        val endMs = System.currentTimeMillis()
        val durationMs = (endNs - startNs) / 1_000_000L

        // Capture response event
        val responseEvent = try {
            createResponseEvent(eventId, request, response, requestEvent, startMs, endMs, durationMs, chain.call())
        } catch (e: Exception) {
            null
        }

        responseEvent?.let { event ->
            scope.launch { collector.emit(event) }
        }

        return response
    }

    private fun createRequestEvent(
        eventId: String,
        request: Request,
        startMs: Long
    ): NetworkEvent {
        val httpUrl = request.url
        val url = httpUrl.toString()
        val method = HttpMethod.from(request.method)
        val headers = request.headers.toMultimap()
        val queryParams = httpUrl.queryParameterNames.associateWith { name ->
            httpUrl.queryParameterValues(name).filterNotNull()
        }

        val requestBodyData = bodyExtractor.extractRequestBody(request)
        val requestSize = requestBodyData?.size ?: request.body?.contentLength()?.coerceAtLeast(0L) ?: 0L

        return NetworkEvent(
            id = eventId,
            timestamp = startMs,
            method = method,
            url = url,
            scheme = httpUrl.scheme,
            host = httpUrl.host,
            port = httpUrl.port,
            path = httpUrl.encodedPath,
            queryParameters = queryParams,
            protocol = null,
            requestHeaders = headers,
            requestBody = requestBodyData,
            requestContentType = request.body?.contentType()?.toString(),
            requestSize = requestSize,
            statusCode = null,
            statusMessage = null,
            responseHeaders = emptyMap(),
            responseBody = null,
            responseContentType = null,
            responseSize = 0L,
            timing = NetworkTiming(startTimestamp = startMs),
            error = null,
            source = NetworkSource.OKHTTP,
            state = NetworkEventState.REQUEST_CAPTURED
        )
    }

    private fun createResponseEvent(
        eventId: String,
        request: Request,
        response: Response,
        requestEvent: NetworkEvent?,
        startMs: Long,
        endMs: Long,
        durationMs: Long,
        call: okhttp3.Call
    ): NetworkEvent {
        val headers = response.headers.toMultimap()
        val responseBodyData = bodyExtractor.extractResponseBody(response)
        val responseSize = responseBodyData?.size ?: response.body?.contentLength()?.coerceAtLeast(0L) ?: 0L
        
        val detailedTiming = timingCapture?.getTimingForCall(call)
        val finalTiming = detailedTiming ?: NetworkTiming(startTimestamp = startMs, endTimestamp = endMs)

        val error = NetworkError.fromStatusCode(response.code)

        return requestEvent?.copy(
            state = NetworkEventState.COMPLETED,
            protocol = response.protocol.toString(),
            statusCode = response.code,
            statusMessage = response.message,
            responseHeaders = headers,
            responseBody = responseBodyData,
            responseContentType = response.body?.contentType()?.toString(),
            responseSize = responseSize,
            timing = finalTiming,
            error = error
        ) ?: createRequestEvent(eventId, request, startMs).copy(
            state = NetworkEventState.COMPLETED,
            protocol = response.protocol.toString(),
            statusCode = response.code,
            statusMessage = response.message,
            responseHeaders = headers,
            responseBody = responseBodyData,
            responseContentType = response.body?.contentType()?.toString(),
            responseSize = responseSize,
            timing = finalTiming,
            error = error
        )
    }

    private fun createErrorEvent(
        eventId: String,
        request: Request,
        requestEvent: NetworkEvent?,
        e: IOException,
        startMs: Long,
        endMs: Long,
        durationMs: Long
    ): NetworkEvent {
        val networkError = NetworkError.fromThrowable(e)
        val finalTiming = NetworkTiming(startTimestamp = startMs, endTimestamp = endMs)

        return requestEvent?.copy(
            state = NetworkEventState.FAILED,
            timing = finalTiming,
            error = networkError
        ) ?: createRequestEvent(eventId, request, startMs).copy(
            state = NetworkEventState.FAILED,
            timing = finalTiming,
            error = networkError
        )
    }
}
