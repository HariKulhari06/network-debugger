package com.hari.networkdebugger.core.util

import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.NetworkEvent
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility to export a list of [NetworkEvent]s to standard HTTP Archive (HAR) format.
 */
public object HarExporter {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Converts the list of network events into a HAR JSON string.
     */
    public fun exportToHarString(events: List<NetworkEvent>): String {
        return buildJsonObject {
            put("log", buildJsonObject {
                put("version", "1.2")
                put("creator", buildJsonObject {
                    put("name", "NetworkDebugger")
                    put("version", "1.0.0")
                })
                put("entries", buildJsonArray {
                    events.forEach { event ->
                        add(buildJsonObject {
                            val startedDateTime = isoDateFormat.format(Date(event.timestamp))
                            put("startedDateTime", startedDateTime)
                            val totalTime = event.timing.totalMs ?: 0L
                            put("time", totalTime.toDouble())
                            
                            put("request", buildJsonObject {
                                put("method", event.method.name)
                                put("url", event.url)
                                put("httpVersion", event.protocol ?: "HTTP/1.1")
                                
                                put("headers", buildJsonArray {
                                    event.requestHeaders.forEach { (name, values) ->
                                        values.forEach { value ->
                                            add(buildJsonObject {
                                                put("name", name)
                                                put("value", value)
                                            })
                                        }
                                    }
                                })
                                
                                put("queryString", buildJsonArray {
                                    event.queryParameters.forEach { (name, values) ->
                                        values.forEach { value ->
                                            add(buildJsonObject {
                                                put("name", name)
                                                put("value", value)
                                            })
                                        }
                                    }
                                })
                                
                                val bodyText = (event.requestBody as? BodyData.Text)?.content
                                if (bodyText != null) {
                                    put("postData", buildJsonObject {
                                        put("mimeType", event.requestContentType ?: "application/octet-stream")
                                        put("text", bodyText)
                                    })
                                }
                                
                                put("headersSize", -1)
                                put("bodySize", event.requestSize)
                            })
                            
                            put("response", buildJsonObject {
                                put("status", event.statusCode ?: 0)
                                put("statusText", event.statusMessage ?: "")
                                put("httpVersion", event.protocol ?: "HTTP/1.1")
                                
                                put("headers", buildJsonArray {
                                    event.responseHeaders.forEach { (name, values) ->
                                        values.forEach { value ->
                                            add(buildJsonObject {
                                                put("name", name)
                                                put("value", value)
                                            })
                                        }
                                    }
                                })
                                
                                val responseBodyText = (event.responseBody as? BodyData.Text)?.content
                                put("content", buildJsonObject {
                                    put("size", event.responseSize)
                                    put("mimeType", event.responseContentType ?: "application/octet-stream")
                                    if (responseBodyText != null) {
                                        put("text", responseBodyText)
                                    }
                                })
                                
                                put("redirectURL", "")
                                put("headersSize", -1)
                                put("bodySize", event.responseSize)
                            })
                            
                            put("cache", buildJsonObject {})
                            
                            put("timings", buildJsonObject {
                                put("blocked", -1)
                                put("dns", event.timing.dnsMs ?: -1)
                                put("connect", event.timing.connectMs ?: -1)
                                put("ssl", event.timing.tlsMs ?: -1)
                                put("send", 0)
                                put("wait", event.timing.waitingMs ?: -1)
                                put("receive", event.timing.downloadMs ?: -1)
                            })
                        })
                    }
                })
            })
        }.toString()
    }
}
