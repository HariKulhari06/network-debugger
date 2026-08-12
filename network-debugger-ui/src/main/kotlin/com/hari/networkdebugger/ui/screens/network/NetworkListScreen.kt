package com.hari.networkdebugger.ui.screens.network

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.hari.networkdebugger.ui.components.EmptyState
import com.hari.networkdebugger.ui.components.SearchBar
import com.hari.networkdebugger.ui.components.SessionHeader
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkListScreen(
    onEventClick: (String) -> Unit,
    viewModel: NetworkListViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val isSearchVisible by viewModel.isSearchVisible.collectAsState()

    var deleteConfirmationSession by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(colors.surface)) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Network",
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
                                    text = "$totalCount requests",
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
                        IconButton(onClick = { showClearConfirm = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = colors.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surface
                    ),
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
                    title = if (searchQuery.isNotEmpty()) "No matches found" else "Listening for traffic...",
                    subtitle = if (searchQuery.isNotEmpty()) "Try adjusting your search or filters" else "HTTP requests will appear here automatically"
                )
            } else {
                val grouped = remember(events) { events.groupBy { it.sessionId } }
                val collapsedSessions = remember { mutableStateMapOf<String, Boolean>() }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
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
                            items(sessionEvents, key = { it.id }) { event ->
                                RequestRow(
                                    event = event,
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

            if (showClearConfirm) {
                AlertDialog(
                    onDismissRequest = { showClearConfirm = false },
                    title = { Text(text = "Clear All Sessions", color = colors.onSurface) },
                    text = { Text(text = "Are you sure you want to delete all captured network sessions and requests? This action cannot be undone.", color = colors.onSurfaceVariant) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.clearAll()
                                showClearConfirm = false
                            }
                        ) {
                            Text(text = "DELETE ALL", color = colors.status4xx, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearConfirm = false }) {
                            Text(text = "CANCEL", color = colors.onSurfaceVariant)
                        }
                    },
                    containerColor = colors.surfaceVariant
                )
            }
        }
    }
}
