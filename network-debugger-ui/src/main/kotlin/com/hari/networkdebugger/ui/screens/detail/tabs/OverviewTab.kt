package com.hari.networkdebugger.ui.screens.detail.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.core.model.BodyData
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.ui.components.CodeBlock
import com.hari.networkdebugger.ui.components.HeadersSection
import com.hari.networkdebugger.ui.components.JsonSyntaxHighlighter
import com.hari.networkdebugger.ui.components.SectionHeader
import com.hari.networkdebugger.ui.screens.detail.BodyDisplayMode
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun OverviewTab(
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // General Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(title = "General")

            InfoRow(label = "Method", value = event.method.name)
            InfoRow(label = "URL", value = event.url)
            event.protocol?.let { InfoRow(label = "Protocol", value = it) }
            InfoRow(label = "Scheme", value = event.scheme.uppercase())
        }

        // Request Headers
        HeadersSection(
            title = "Request Headers",
            headers = event.requestHeaders,
            onCopy = {
                val text = event.requestHeaders.entries.joinToString("\n") { "${it.key}: ${it.value.joinToString(", ")}" }
                clipboardManager.setText(AnnotatedString(text))
            }
        )

        // Response Headers
        HeadersSection(
            title = "Response Headers",
            headers = event.responseHeaders,
            onCopy = {
                val text = event.responseHeaders.entries.joinToString("\n") { "${it.key}: ${it.value.joinToString(", ")}" }
                clipboardManager.setText(AnnotatedString(text))
            }
        )

        // Response Body
        event.responseBody?.let { body ->
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "Response Body", modifier = Modifier.weight(1f))

                    // Raw / Pretty toggle pills
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceContainer)
                            .padding(2.dp)
                    ) {
                        BodyModeToggle(
                            label = "Raw",
                            isSelected = responseBodyMode == BodyDisplayMode.RAW,
                            onClick = { onResponseBodyModeChange(BodyDisplayMode.RAW) }
                        )
                        BodyModeToggle(
                            label = "Pretty",
                            isSelected = responseBodyMode == BodyDisplayMode.PRETTY,
                            onClick = { onResponseBodyModeChange(BodyDisplayMode.PRETTY) }
                        )
                    }
                }

                when (body) {
                    is BodyData.Text -> {
                        val content = if (responseBodyMode == BodyDisplayMode.PRETTY) {
                            JsonSyntaxHighlighter.formatAndHighlight(body.content).text
                        } else {
                            body.content
                        }
                        CodeBlock(
                            content = content,
                            onCopy = { clipboardManager.setText(AnnotatedString(body.content)) }
                        )
                    }
                    is BodyData.Binary -> {
                        CodeBlock(content = "Binary response (${body.size} bytes)\nPreview unavailable")
                    }
                    is BodyData.Truncated -> {
                        CodeBlock(content = "Body truncated (showing ${body.capturedSize} of ${body.actualSize} bytes)")
                    }
                    is BodyData.FileReference -> {
                        CodeBlock(content = "Stored in file: ${body.path}")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colors = LocalDebuggerColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            color = colors.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            color = colors.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BodyModeToggle(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalDebuggerColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) colors.primaryContainer else colors.surfaceContainer)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) colors.primary else colors.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
