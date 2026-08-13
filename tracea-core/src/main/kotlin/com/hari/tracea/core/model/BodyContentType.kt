package com.hari.tracea.core.model

import java.util.Locale

/**
 * Broad categories of content types.
 */
enum class BodyContentType {
    JSON, TEXT, XML, HTML, FORM, MULTIPART, IMAGE, VIDEO, AUDIO, BINARY, UNKNOWN;

    companion object {
        /**
         * Detects content type from MIME string.
         */
        fun fromContentType(contentType: String?): BodyContentType {
            if (contentType == null) return UNKNOWN
            val lower = contentType.lowercase(Locale.ROOT)
            return when {
                lower.contains("json") -> JSON
                lower.contains("xml") -> XML
                lower.contains("html") -> HTML
                lower.contains("form-urlencoded") -> FORM
                lower.contains("multipart") -> MULTIPART
                lower.startsWith("image/") -> IMAGE
                lower.startsWith("video/") -> VIDEO
                lower.startsWith("audio/") -> AUDIO
                lower.startsWith("text/") -> TEXT
                lower.contains("octet-stream") || lower.contains("binary") -> BINARY
                else -> UNKNOWN
            }
        }
    }
}
