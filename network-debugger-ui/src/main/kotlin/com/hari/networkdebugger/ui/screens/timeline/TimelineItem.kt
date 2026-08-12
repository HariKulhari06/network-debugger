package com.hari.networkdebugger.ui.screens.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.util.DurationFormatter
import com.hari.networkdebugger.ui.components.MethodBadge
import com.hari.networkdebugger.ui.components.StatusBadge
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineItem(
    event: NetworkEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(event.timestamp))

    val dotColor = colors.statusColor(event.statusCode ?: 0)
    val outlineColor = colors.outline.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick)
            .background(colors.surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Timestamp
        Text(
            text = formattedTime,
            color = colors.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(76.dp)
        )

        // Center Column: Connecting Line + Circle Dot
        Box(
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                // Vertical line
                drawLine(
                    color = outlineColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                // Background for dot to create a gap in the line
                drawCircle(
                    color = colors.surface,
                    radius = 6.dp.toPx(),
                    center = Offset(centerX, size.height / 2)
                )
                // Status dot
                drawCircle(
                    color = dotColor,
                    radius = 4.dp.toPx(),
                    center = Offset(centerX, size.height / 2)
                )
            }
        }

        // Right Column: Request Details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MethodBadge(method = event.method)
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Text(
                    text = event.path,
                    color = colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                
                event.statusCode?.let { code ->
                    StatusBadge(statusCode = code)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.host,
                    color = colors.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                event.timing.totalMs?.let { duration ->
                    Text(
                        text = DurationFormatter.format(duration),
                        color = colors.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
