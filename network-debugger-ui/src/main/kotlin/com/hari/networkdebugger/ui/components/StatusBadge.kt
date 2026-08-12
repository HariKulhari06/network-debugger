package com.hari.networkdebugger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun StatusBadge(
    statusCode: Int,
    statusMessage: String? = null,
    showMessage: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val text = if (showMessage && statusMessage != null) "$statusCode $statusMessage" else statusCode.toString()
    val contentColor = colors.statusColor(statusCode)
    val containerColor = colors.statusContainerColor(statusCode)

    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(6.dp))
            .border(0.5.dp, contentColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
