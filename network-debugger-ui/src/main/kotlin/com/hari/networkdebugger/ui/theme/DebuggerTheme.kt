package com.hari.networkdebugger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun DebuggerTheme(content: @Composable () -> Unit) {
    val colors = DebuggerColorScheme()
    val materialColors = darkColorScheme(
        background = colors.surface,
        surface = colors.surface,
        surfaceVariant = colors.surfaceVariant,
        onSurface = colors.onSurface,
        onSurfaceVariant = colors.onSurfaceVariant,
        primary = colors.primary,
        outline = colors.outline
    )

    CompositionLocalProvider(LocalDebuggerColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}
