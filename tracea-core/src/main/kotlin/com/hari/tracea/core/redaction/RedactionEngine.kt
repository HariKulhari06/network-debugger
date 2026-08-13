package com.hari.tracea.core.redaction

import com.hari.tracea.core.config.RedactionConfig
import com.hari.tracea.core.model.BodyData
import com.hari.tracea.core.model.NetworkEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Locale

/**
 * Handles redaction of sensitive data from network events.
 */
class RedactionEngine(private val config: RedactionConfig) {
    
    private val sensitiveHeadersLower = config.sensitiveHeaders.map { it.lowercase(Locale.ROOT) }.toSet()
    private val sensitiveJsonFieldsLower = config.sensitiveJsonFields.map { it.lowercase(Locale.ROOT) }.toSet()

    /**
     * Redacts sensitive headers.
     */
    fun redactHeaders(headers: Map<String, List<String>>): Map<String, List<String>> {
        val redacted = mutableMapOf<String, List<String>>()
        for ((key, value) in headers) {
            if (sensitiveHeadersLower.contains(key.lowercase(Locale.ROOT))) {
                redacted[key] = value.map { config.replacementString }
            } else {
                redacted[key] = value
            }
        }
        return redacted
    }

    /**
     * Redacts sensitive fields from a JSON body.
     */
    fun redactJsonBody(json: String): String {
        return try {
            val element = Json.parseToJsonElement(json)
            val redactedElement = redactElement(element)
            redactedElement.toString()
        } catch (e: Exception) {
            // If it's not valid JSON, leave it as is.
            json
        }
    }

    private fun redactElement(element: JsonElement): JsonElement {
        return when (element) {
            is JsonObject -> {
                val redactedMap = element.mapValues { (key, value) ->
                    if (sensitiveJsonFieldsLower.contains(key.lowercase(Locale.ROOT))) {
                        JsonPrimitive(config.replacementString)
                    } else {
                        redactElement(value)
                    }
                }
                JsonObject(redactedMap)
            }
            is JsonArray -> {
                JsonArray(element.map { redactElement(it) })
            }
            is JsonPrimitive -> element
        }
    }

    /**
     * Redacts an entire NetworkEvent.
     */
    fun redactEvent(event: NetworkEvent): NetworkEvent {
        val reqHeaders = redactHeaders(event.requestHeaders)
        val resHeaders = redactHeaders(event.responseHeaders)
        
        val reqBody = if (event.requestBody is BodyData.Text && event.requestContentType?.lowercase()?.contains("json") == true) {
            BodyData.Text(redactJsonBody(event.requestBody.content), event.requestBody.contentType, event.requestBody.size)
        } else {
            event.requestBody
        }
        
        val resBody = if (event.responseBody is BodyData.Text && event.responseContentType?.lowercase()?.contains("json") == true) {
            BodyData.Text(redactJsonBody(event.responseBody.content), event.responseBody.contentType, event.responseBody.size)
        } else {
            event.responseBody
        }
        
        return event.copy(
            requestHeaders = reqHeaders,
            responseHeaders = resHeaders,
            requestBody = reqBody,
            responseBody = resBody
        )
    }
}
