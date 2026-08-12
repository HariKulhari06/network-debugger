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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hari.networkdebugger.ui.components.EmptyState
import com.hari.networkdebugger.ui.components.SearchBar
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkListScreen(
    onEventClick: (String) -> Unit,
    viewModel: NetworkListViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val colors = LocalDebuggerColors.current
    val events by viewModel.events.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val isSearchVisible by viewModel.isSearchVisible.collectAsState()

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
                        IconButton(onClick = { viewModel.clearAll() }) {
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(events, key = { it.id }) { event ->
                        RequestRow(
                            event = event,
                            onClick = { onEventClick(event.id) }
                        )
                    }
                }
            }
        }
    }
}
