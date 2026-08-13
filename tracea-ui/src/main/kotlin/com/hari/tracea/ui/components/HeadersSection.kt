package com.hari.tracea.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeadersSection(
    title: String,
    headers: Map<String, List<String>>,
    modifier: Modifier = Modifier,
    onCopy: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    if (headers.isEmpty()) return

    val displayHeaders = if (expanded || headers.size <= 5) {
        headers
    } else {
        headers.entries.take(5).associate { it.key to it.value }
    }

    val formattedHeaders = displayHeaders.entries.joinToString("\n") { (key, values) ->
        "$key: ${values.joinToString(", ")}"
    }

    val actionText = if (headers.size > 5) {
        if (expanded) "Show Less" else "View All (${headers.size})"
    } else null

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        SectionHeader(
            title = title,
            action = actionText,
            onAction = { expanded = !expanded }
        )
        CodeBlock(
            content = formattedHeaders,
            onCopy = onCopy
        )
    }
}
