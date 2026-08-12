package com.hari.networkdebugger.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.core.util.DurationFormatter
import com.hari.networkdebugger.ui.DebuggerServiceLocator
import com.hari.networkdebugger.ui.screens.network.StatusFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SessionStats(
    val totalRequests: Int = 0,
    val formattedDuration: String = "00:00:00",
    val slowestFormatted: String = "N/A"
)

class TimelineViewModel : ViewModel() {

    private val store = DebuggerServiceLocator.store

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _activeFilter = MutableStateFlow(StatusFilter.ALL)
    val activeFilter: StateFlow<StatusFilter> = _activeFilter

    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible

    val events: StateFlow<List<NetworkEvent>> = combine(
        store?.getAll() ?: MutableStateFlow(emptyList()),
        _searchQuery,
        _activeFilter
    ) { allEvents, query, filter ->
        allEvents.filter { event ->
            val matchesQuery = query.isBlank() ||
                    event.url.contains(query, ignoreCase = true) ||
                    event.host.contains(query, ignoreCase = true) ||
                    event.method.name.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                StatusFilter.ALL -> true
                StatusFilter.SUCCESS_2XX -> (event.statusCode ?: 0) in 200..299
                StatusFilter.REDIRECT_3XX -> (event.statusCode ?: 0) in 300..399
                StatusFilter.CLIENT_ERROR_4XX -> (event.statusCode ?: 0) in 400..499
                StatusFilter.SERVER_ERROR_5XX -> (event.statusCode ?: 0) in 500..599
                StatusFilter.ERRORS -> (event.statusCode ?: 0) >= 400 || event.error != null
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sessionStats: StateFlow<SessionStats> = (store?.getAll() ?: MutableStateFlow(emptyList<NetworkEvent>())).map { allEvents ->
        if (allEvents.isEmpty()) {
            SessionStats()
        } else {
            val total = allEvents.size
            val minTime = allEvents.minOf { it.timestamp }
            val maxTime = System.currentTimeMillis()
            val diffSec = (maxTime - minTime) / 1000

            val hours = diffSec / 3600
            val minutes = (diffSec % 3600) / 60
            val seconds = diffSec % 60
            val durationStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            val slowestMs = allEvents.mapNotNull { it.timing.totalMs }.maxOrNull()
            val slowestStr = slowestMs?.let { DurationFormatter.format(it) } ?: "N/A"

            SessionStats(totalRequests = total, formattedDuration = durationStr, slowestFormatted = slowestStr)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, SessionStats())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: StatusFilter) {
        _activeFilter.value = filter
    }

    fun toggleSearch() {
        _isSearchVisible.value = !_isSearchVisible.value
        if (!_isSearchVisible.value) _searchQuery.value = ""
    }

    fun clearAll() {
        viewModelScope.launch {
            store?.clear()
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            store?.deleteSession(sessionId)
        }
    }

    fun exportSessionHar(context: android.content.Context, sessionId: String, sessionName: String) {
        viewModelScope.launch {
            val sessionEvents = store?.getSessionEvents(sessionId) ?: emptyList()
            com.hari.networkdebugger.ui.util.HarSharer.shareSessionHar(context, sessionName, sessionEvents)
        }
    }
}
