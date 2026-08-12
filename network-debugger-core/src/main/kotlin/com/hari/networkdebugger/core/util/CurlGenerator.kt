package com.hari.networkdebugger.core.util

import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.NetworkEvent

/**
 * Generates cURL commands from NetworkEvents.
 */
object CurlGenerator {
    
    fun generate(event: NetworkEvent): String {
        val builder = java.lang.StringBuilder("curl -X ${event.method.name}")
        
        event.requestHeaders.forEach { (name, values) ->
            values.forEach { value ->
                // Escape single quotes in headers
                val escapedValue = value.replace("'", "\\'")
                builder.append(" -H '$name: $escapedValue'")
            }
        }
        
        val body = event.requestBody
        if (body is BodyData.Text) {
            val content = body.content.replace("'", "\\'")
            builder.append(" -d '$content'")
        }
        
        builder.append(" '${event.url}'")
        return builder.toString()
    }
}
