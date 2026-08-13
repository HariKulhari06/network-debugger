package com.hari.tracea.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.tracea.ui.theme.LocalDebuggerColors

@Composable
fun SummaryCardsRow(
    status: String,
    duration: String,
    size: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard("STATUS", status, Modifier.weight(1f))
        SummaryCard("DURATION", duration, Modifier.weight(1f))
        SummaryCard("SIZE", size, Modifier.weight(1f))
        SummaryCard("TIME", time, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .border(0.5.dp, colors.outline, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = colors.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            color = colors.onSurface,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}
