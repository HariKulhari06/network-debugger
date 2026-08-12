package com.hari.networkdebugger.okhttp

import com.hari.networkdebugger.core.config.BodyCaptureConfig
import com.hari.networkdebugger.core.model.BodyContentType
import com.hari.networkdebugger.core.model.BodyData
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.StandardCharsets

/**
 * Utility to safely extract HTTP bodies for the Network Debugger without
 * consuming the streams meant for the application.
 */
internal class OkHttpBodyExtractor(private val config: BodyCaptureConfig) {

    fun extractRequestBody(request: Request): BodyData? {
        val body = request.body ?: return null
        
        val contentType = body.contentType()
        val mediaTypeString = contentType?.toString()
        val parsedContentType = parseContentType(mediaTypeString)
        
        if (parsedContentType == BodyContentType.BINARY && !config.captureBinary) {
            return BodyData.Binary(size = body.contentLength().coerceAtLeast(0L), contentType = parsedContentType)
        }
        
        try {
            val buffer = Buffer()
            body.writeTo(buffer)
            
            val size = buffer.size
            if (size > config.maxRequestBodySize) {
                return BodyData.Truncated(actualSize = size, capturedSize = config.maxRequestBodySize, contentType = parsedContentType)
            }
            
            val charset = contentType?.charset() ?: StandardCharsets.UTF_8
            
            return when (parsedContentType) {
                BodyContentType.BINARY, BodyContentType.IMAGE, BodyContentType.VIDEO, BodyContentType.AUDIO -> {
                    BodyData.Binary(size = size, contentType = parsedContentType)
                }
                else -> {
                    val text = buffer.readString(charset)
                    BodyData.Text(content = text, contentType = parsedContentType, size = size)
                }
            }
        } catch (e: Exception) {
            return BodyData.Binary(size = 0L, contentType = parsedContentType)
        }
    }

    fun extractResponseBody(response: Response): BodyData? {
        val body = response.body ?: return null
        
        val contentType = body.contentType()
        val mediaTypeString = contentType?.toString()
        val parsedContentType = parseContentType(mediaTypeString)
        
        if (parsedContentType == BodyContentType.BINARY && !config.captureBinary) {
            return BodyData.Binary(size = body.contentLength().coerceAtLeast(0L), contentType = parsedContentType)
        }
        
        try {
            val peekedBody = response.peekBody(config.maxResponseBodySize)
            val contentBytes = peekedBody.bytes()
            val size = contentBytes.size.toLong()
            
            if (size >= config.maxResponseBodySize) {
                return BodyData.Truncated(actualSize = body.contentLength().coerceAtLeast(size), capturedSize = size, contentType = parsedContentType)
            }
            
            if (contentBytes.isEmpty()) {
                return BodyData.Text(content = "", contentType = parsedContentType, size = 0L)
            }
            
            val charset = contentType?.charset() ?: StandardCharsets.UTF_8
            
            return when (parsedContentType) {
                BodyContentType.BINARY, BodyContentType.IMAGE, BodyContentType.VIDEO, BodyContentType.AUDIO -> {
                    BodyData.Binary(size = size, contentType = parsedContentType)
                }
                else -> {
                    val text = String(contentBytes, charset)
                    BodyData.Text(content = text, contentType = parsedContentType, size = size)
                }
            }
        } catch (e: Exception) {
            return BodyData.Binary(size = 0L, contentType = parsedContentType)
        }
    }
    
    private fun parseContentType(mediaType: String?): BodyContentType {
        if (mediaType == null) return BodyContentType.UNKNOWN
        
        val lowerType = mediaType.lowercase()
        return when {
            lowerType.contains("application/json") -> BodyContentType.JSON
            lowerType.contains("application/xml") || lowerType.contains("text/xml") -> BodyContentType.XML
            lowerType.contains("text/html") -> BodyContentType.HTML
            lowerType.contains("application/x-www-form-urlencoded") -> BodyContentType.FORM
            lowerType.contains("multipart/") -> BodyContentType.MULTIPART
            lowerType.contains("image/") -> BodyContentType.IMAGE
            lowerType.contains("video/") -> BodyContentType.VIDEO
            lowerType.contains("audio/") -> BodyContentType.AUDIO
            lowerType.contains("text/") -> BodyContentType.TEXT
            else -> BodyContentType.BINARY
        }
    }
}
