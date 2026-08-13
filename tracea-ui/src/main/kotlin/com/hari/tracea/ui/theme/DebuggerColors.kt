package com.hari.tracea.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.hari.tracea.core.model.HttpMethod

data class DebuggerColorScheme(
    val surface: Color = Color(0xFF0F111A),
    val surfaceVariant: Color = Color(0xFF1E202E),
    val surfaceContainer: Color = Color(0xFF25283B),
    val onSurface: Color = Color(0xFFE2E4F3),
    val onSurfaceVariant: Color = Color(0xFF9499B8),
    val primary: Color = Color(0xFF7E97FF),
    val primaryContainer: Color = Color(0xFF2D3561),
    val onPrimaryContainer: Color = Color(0xFFD6DFFF),
    val outline: Color = Color(0xFF30344D),
    val sectionHeader: Color = Color(0xFFA6ACCD),
    val methodGet: Color = Color(0xFF4FD6BE),
    val methodPost: Color = Color(0xFF7E97FF),
    val methodPut: Color = Color(0xFFFFCB6B),
    val methodDelete: Color = Color(0xFFF07178),
    val methodPatch: Color = Color(0xFFC792EA),
    val status2xx: Color = Color(0xFF4FD6BE),
    val status2xxContainer: Color = Color(0xFF1B3D37),
    val status3xx: Color = Color(0xFF82AAFF),
    val status3xxContainer: Color = Color(0xFF253352),
    val status4xx: Color = Color(0xFFF07178),
    val status4xxContainer: Color = Color(0xFF45272E),
    val status5xx: Color = Color(0xFFFF5370),
    val status5xxContainer: Color = Color(0xFF4A1F26),
    val liveDot: Color = Color(0xFF4FD6BE),
    val errorDot: Color = Color(0xFFF07178)
) {
    fun methodColor(method: HttpMethod): Color = when(method) {
        HttpMethod.GET -> methodGet
        HttpMethod.POST -> methodPost
        HttpMethod.PUT -> methodPut
        HttpMethod.DELETE -> methodDelete
        HttpMethod.PATCH -> methodPatch
        else -> onSurfaceVariant
    }

    fun statusColor(statusCode: Int): Color = when(statusCode) {
        in 200..299 -> status2xx
        in 300..399 -> status3xx
        in 400..499 -> status4xx
        in 500..599 -> status5xx
        else -> onSurfaceVariant
    }

    fun statusContainerColor(statusCode: Int): Color = when(statusCode) {
        in 200..299 -> status2xxContainer
        in 300..399 -> status3xxContainer
        in 400..499 -> status4xxContainer
        in 500..599 -> status5xxContainer
        else -> surfaceContainer
    }
}

val LocalDebuggerColors = staticCompositionLocalOf { DebuggerColorScheme() }
