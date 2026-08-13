package com.hari.tracea.ui.screens.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hari.tracea.ui.components.EmptyState
import com.hari.tracea.ui.components.SearchBar
import com.hari.tracea.ui.components.SessionHeader
import com.hari.tracea.ui.screens.network.StatusFilterChips
import com.hari.tracea.ui.theme.LocalDebuggerColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onEventClick: (String) -> Unit = {},
    viewModel: TimelineViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val stats by viewModel.sessionStats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val isSearchVisible by viewModel.isSearchVisible.collectAsState()

    var deleteConfirmationSession by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(colors.surface)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Timeline",
                                color = colors.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(colors.liveDot)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live • ${events.size} items",
                                    color = colors.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = colors.onSurface)
                        }
                        IconButton(onClick = { viewModel.clearAll() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all", tint = colors.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface),
                    windowInsets = WindowInsets.statusBars
                )

                SearchBar(
                    visible = isSearchVisible,
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onClose = { viewModel.toggleSearch() }
                )

                StatusFilterChips(
                    selectedFilter = activeFilter,
                    onFilterSelected = { viewModel.setFilter(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceVariant)
            ) {
                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), thickness = 0.5.dp)
                
                // Color Legend Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = colors.status2xx, label = "2xx")
                    LegendItem(color = colors.status3xx, label = "3xx")
                    LegendItem(color = colors.status4xx, label = "4xx")
                    LegendItem(color = colors.status5xx, label = "5xx")
                }

                HorizontalDivider(color = colors.outline.copy(alpha = 0.3f), thickness = 0.5.dp)

                // Session Summary Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatsItem(label = "REQUESTS", value = stats.totalRequests.toString())
                    StatsItem(label = "DURATION", value = stats.formattedDuration)
                    StatsItem(label = "SLOWEST", value = stats.slowestFormatted)
                }
            }
        },
        containerColor = colors.surface,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (events.isEmpty()) {
                EmptyState(
                    title = "No activity recorded",
                    subtitle = "Network transactions will appear here as they happen"
                )
            } else {
                val grouped = remember(events) { events.groupBy { it.sessionId } }
                val collapsedSessions = remember { mutableStateMapOf<String, Boolean>() }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    grouped.forEach { (sessionId, sessionEvents) ->
                        val sessionName = sessionEvents.firstOrNull()?.sessionName ?: "Unknown Session"
                        val isCollapsed = collapsedSessions[sessionId] ?: false

                        item(key = sessionId) {
                            SessionHeader(
                                name = sessionName,
                                requestCount = sessionEvents.size,
                                isCollapsed = isCollapsed,
                                onToggle = { collapsedSessions[sessionId] = !isCollapsed },
                                onShareClick = { viewModel.exportSessionHar(context, sessionId, sessionName) },
                                onDeleteClick = { deleteConfirmationSession = Pair(sessionId, sessionName) }
                            )
                        }

                        if (!isCollapsed) {
                            val sessionStart = sessionEvents.minOfOrNull { it.timestamp } ?: 0L
                            val sessionEnd = sessionEvents.maxOfOrNull { it.timing.endTimestamp ?: (it.timestamp + (it.timing.totalMs ?: 0L)) } ?: 0L
                            val sessionDuration = maxOf(1L, sessionEnd - sessionStart)

                            items(sessionEvents, key = { it.id }) { event ->
                                TimelineItem(
                                    event = event,
                                    sessionStart = sessionStart,
                                    sessionDuration = sessionDuration,
                                    onClick = { onEventClick(event.id) }
                                )
                            }
                        }
                    }
                }
            }

            deleteConfirmationSession?.let { (sessionId, sessionName) ->
                AlertDialog(
                    onDismissRequest = { deleteConfirmationSession = null },
                    title = { Text(text = "Delete Session", color = colors.onSurface) },
                    text = { Text(text = "Are you sure you want to delete '$sessionName'? This will permanently remove all its network logs.", color = colors.onSurfaceVariant) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteSession(sessionId)
                                deleteConfirmationSession = null
                            }
                        ) {
                            Text(text = "DELETE", color = colors.status4xx, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deleteConfirmationSession = null }) {
                            Text(text = "CANCEL", color = colors.onSurfaceVariant)
                        }
                    },
                    containerColor = colors.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    val colors = LocalDebuggerColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label, 
            color = colors.onSurfaceVariant, 
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatsItem(label: String, value: String) {
    val colors = LocalDebuggerColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = colors.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            color = colors.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
