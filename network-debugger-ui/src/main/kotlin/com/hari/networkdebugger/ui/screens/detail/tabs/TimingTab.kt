package com.hari.networkdebugger.ui.screens.detail.tabs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.util.DurationFormatter
import com.hari.networkdebugger.ui.components.SectionHeader
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun TimingTab(
    event: NetworkEvent,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val scrollState = rememberScrollState()
    val timing = event.timing

    val phases = listOfNotNull(
        timing.dnsMs?.let { TimingPhase("DNS Lookup", it, colors.primary) },
        timing.connectMs?.let { TimingPhase("TCP Connection", it, colors.methodPut) },
        timing.tlsMs?.let { TimingPhase("TLS Handshake", it, colors.methodPatch) },
        timing.waitingMs?.let { TimingPhase("Waiting (TTFB)", it, colors.methodGet) },
        timing.downloadMs?.let { TimingPhase("Content Download", it, colors.status3xx) }
    )

    val maxPhaseMs = phases.maxOfOrNull { it.ms }?.coerceAtLeast(1L) ?: 1L

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(title = "Network Timing")

            if (phases.isEmpty()) {
                timing.totalMs?.let { total ->
                    TimingRow(label = "Total Duration", ms = total, maxMs = total, color = colors.primary)
                } ?: Text(
                    text = "No granular timing data available", 
                    color = colors.onSurfaceVariant, 
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    phases.forEach { phase ->
                        TimingRow(label = phase.label, ms = phase.ms, maxMs = maxPhaseMs, color = phase.color)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), thickness = 0.5.dp)

                    timing.totalMs?.let { total ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Time", 
                                color = colors.onSurface, 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = DurationFormatter.format(total), 
                                color = colors.primary, 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
        
        // Hint/Legend
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Text(
                text = "Timing values are estimated based on interceptor hooks. TTFB includes server processing time.",
                color = colors.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

private data class TimingPhase(val label: String, val ms: Long, val color: Color)

@Composable
private fun TimingRow(
    label: String,
    ms: Long,
    maxMs: Long,
    color: Color
) {
    val colors = LocalDebuggerColors.current
    val fraction = (ms.toFloat() / maxMs.toFloat()).coerceIn(0.02f, 1.0f)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = colors.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = DurationFormatter.format(ms),
                color = colors.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(colors.surfaceContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
