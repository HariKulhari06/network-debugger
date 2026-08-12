package com.hari.networkdebugger.ui.screens.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.util.SizeFormatter
import com.hari.networkdebugger.ui.components.CodeBlock
import com.hari.networkdebugger.ui.components.HeadersSection
import com.hari.networkdebugger.ui.components.JsonSyntaxHighlighter
import com.hari.networkdebugger.ui.components.SectionHeader
import com.hari.networkdebugger.ui.components.StatusBadge
import com.hari.networkdebugger.ui.screens.detail.BodyDisplayMode
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun ResponseTab(
    event: NetworkEvent,
    responseBodyMode: BodyDisplayMode,
    onResponseBodyModeChange: (BodyDisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text("Status Code", color = colors.onSurfaceVariant, fontSize = 12.sp)
                event.statusCode?.let { code ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        StatusBadge(statusCode = code, statusMessage = event.statusMessage, showMessage = true)
                    }
                } ?: Text("No Status", color = colors.onSurfaceVariant, fontSize = 14.sp)
            }

            Column {
                Text("Content Size", color = colors.onSurfaceVariant, fontSize = 12.sp)
                Text(SizeFormatter.format(event.responseSize), color = colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        event.responseContentType?.let { contentType ->
            Column {
                Text("Content Type", color = colors.onSurfaceVariant, fontSize = 12.sp)
                Text(contentType, color = colors.onSurface, fontSize = 13.sp)
            }
        }

        // Response Headers
        HeadersSection(
            title = "Headers",
            headers = event.responseHeaders,
            onCopy = {
                val text = event.responseHeaders.entries.joinToString("\n") { "${it.key}: ${it.value.joinToString(", ")}" }
                clipboardManager.setText(AnnotatedString(text))
            }
        )

        // Cookies
        val cookieHeaders = event.responseHeaders.filter { it.key.equals("Set-Cookie", ignoreCase = true) }
        if (cookieHeaders.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionHeader(title = "Cookies")
                val cookieText = cookieHeaders.values.flatten().joinToString("\n")
                CodeBlock(content = cookieText)
            }
        }

        // Response Body
        event.responseBody?.let { body ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionHeader(title = "Response Body")
                when (body) {
                    is BodyData.Text -> {
                        val formatted = if (responseBodyMode == BodyDisplayMode.PRETTY) {
                            JsonSyntaxHighlighter.formatAndHighlight(body.content).text
                        } else {
                            body.content
                        }
                        CodeBlock(content = formatted, onCopy = { clipboardManager.setText(AnnotatedString(body.content)) })
                    }
                    is BodyData.Binary -> CodeBlock(content = "Binary response (${body.size} bytes)\nPreview unavailable")
                    is BodyData.Truncated -> CodeBlock(content = "Truncated body (showing ${body.capturedSize} of ${body.actualSize} bytes)")
                    is BodyData.FileReference -> CodeBlock(content = "Stored in file: ${body.path}")
                }
            }
        }
    }
}
