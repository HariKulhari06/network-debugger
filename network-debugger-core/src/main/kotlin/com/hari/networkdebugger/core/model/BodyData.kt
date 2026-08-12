package com.hari.networkdebugger.core.model

/**
 * Represents body data for requests or responses.
 */
sealed class BodyData {
    abstract val contentType: BodyContentType
    abstract val size: Long
    
    data class Text(
        val content: String,
        override val contentType: BodyContentType,
        override val size: Long
    ) : BodyData()
    
    data class FileReference(
        val path: String,
        override val contentType: BodyContentType,
        override val size: Long
    ) : BodyData()
    
    data class Truncated(
        val actualSize: Long,
        val capturedSize: Long,
        override val contentType: BodyContentType
    ) : BodyData() {
        override val size: Long get() = actualSize
    }
    
    data class Binary(
        override val size: Long,
        override val contentType: BodyContentType
    ) : BodyData()
}
