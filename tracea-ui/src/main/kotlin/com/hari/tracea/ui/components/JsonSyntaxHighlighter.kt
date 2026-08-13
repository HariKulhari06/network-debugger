package com.hari.tracea.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object JsonSyntaxHighlighter {

    private val colorKey = Color(0xFF7C6EF6)       // Purple
    private val colorString = Color(0xFF4CAF50)    // Green
    private val colorNumber = Color(0xFF5B8DEF)    // Blue
    private val colorBoolean = Color(0xFFFF9800)   // Orange
    private val colorNull = Color(0xFFFF9800)      // Orange
    private val colorDelimiter = Color(0xFF9E9E9E) // Gray

    private val jsonFormatter = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun formatAndHighlight(rawJson: String): AnnotatedString {
        val pretty = try {
            val element = jsonFormatter.parseToJsonElement(rawJson)
            jsonFormatter.encodeToString(JsonElement.serializer(), element)
        } catch (e: Exception) {
            rawJson
        }

        return buildAnnotatedString {
            var i = 0
            val length = pretty.length
            var inString = false
            var isKey = false

            while (i < length) {
                val ch = pretty[i]

                when {
                    ch == '"' -> {
                        val start = i
                        i++
                        while (i < length && (pretty[i] != '"' || pretty[i - 1] == '\\')) {
                            i++
                        }
                        if (i < length) i++ // Include closing quote
                        val strVal = pretty.substring(start, i)

                        // Check if key (followed by optional spaces and colon)
                        var peek = i
                        while (peek < length && pretty[peek].isWhitespace()) peek++
                        isKey = peek < length && pretty[peek] == ':'

                        val style = if (isKey) colorKey else colorString
                        withStyle(SpanStyle(color = style)) {
                            append(strVal)
                        }
                    }
                    ch.isDigit() || (ch == '-' && i + 1 < length && pretty[i + 1].isDigit()) -> {
                        val start = i
                        while (i < length && (pretty[i].isDigit() || pretty[i] == '.' || pretty[i] == 'e' || pretty[i] == 'E' || pretty[i] == '-' || pretty[i] == '+')) {
                            i++
                        }
                        val numVal = pretty.substring(start, i)
                        withStyle(SpanStyle(color = colorNumber)) {
                            append(numVal)
                        }
                    }
                    pretty.startsWith("true", i) -> {
                        withStyle(SpanStyle(color = colorBoolean)) { append("true") }
                        i += 4
                    }
                    pretty.startsWith("false", i) -> {
                        withStyle(SpanStyle(color = colorBoolean)) { append("false") }
                        i += 5
                    }
                    pretty.startsWith("null", i) -> {
                        withStyle(SpanStyle(color = colorNull)) { append("null") }
                        i += 4
                    }
                    ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ':' || ch == ',' -> {
                        withStyle(SpanStyle(color = colorDelimiter)) { append(ch) }
                        i++
                    }
                    else -> {
                        append(ch)
                        i++
                    }
                }
            }
        }
    }
}
