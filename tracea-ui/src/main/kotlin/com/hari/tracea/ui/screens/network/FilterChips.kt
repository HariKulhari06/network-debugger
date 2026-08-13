package com.hari.tracea.ui.screens.network

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

enum class StatusFilter(val label: String) {
    ALL("All"),
    SUCCESS_2XX("2xx"),
    REDIRECT_3XX("3xx"),
    CLIENT_ERROR_4XX("4xx"),
    SERVER_ERROR_5XX("5xx"),
    ERRORS("Errors")
}

@Composable
fun StatusFilterChips(
    selectedFilter: StatusFilter,
    onFilterSelected: (StatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(StatusFilter.values()) { filter ->
            val isSelected = filter == selectedFilter

            val backgroundColor = if (isSelected) colors.primary else colors.surfaceContainer
            val textColor = if (isSelected) colors.onSurface else colors.onSurfaceVariant

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (filter == StatusFilter.ERRORS) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.errorDot)
                        )
                    }
                    Text(
                        text = filter.label,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
