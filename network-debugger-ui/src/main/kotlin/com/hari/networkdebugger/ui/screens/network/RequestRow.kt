package com.hari.networkdebugger.ui.screens.network

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.util.DurationFormatter
import com.hari.networkdebugger.core.util.SizeFormatter
import com.hari.networkdebugger.ui.components.MethodBadge
import com.hari.networkdebugger.ui.components.StatusBadge
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RequestRow(
    event: NetworkEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(event.timestamp))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(colors.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Row: Method, Path, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MethodBadge(method = event.method)
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = event.path + if (event.queryParameters.isNotEmpty()) "?..." else "",
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

            Spacer(modifier = Modifier.height(6.dp))

            // Middle Row: URL/Host
            Text(
                text = event.url,
                color = colors.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Row: Time, Size, Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedTime,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val totalSize = event.requestSize + event.responseSize
                    InfoChip(text = SizeFormatter.format(totalSize))
                    
                    event.timing.totalMs?.let { duration ->
                        InfoChip(text = DurationFormatter.format(duration))
                    }
                }
            }
        }
        HorizontalDivider(
            color = colors.outline.copy(alpha = 0.5f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    val colors = LocalDebuggerColors.current
    Text(
        text = text,
        color = colors.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
    )
}
