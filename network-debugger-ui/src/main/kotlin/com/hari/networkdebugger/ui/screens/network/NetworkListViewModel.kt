package com.hari.networkdebugger.ui.screens.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hari.networkdebugger.core.model.NetworkEvent
import com.hari.networkdebugger.ui.DebuggerServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NetworkListViewModel : ViewModel() {

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
            // Filter by search query
            val matchesQuery = query.isBlank() ||
                    event.url.contains(query, ignoreCase = true) ||
                    event.host.contains(query, ignoreCase = true) ||
                    event.method.name.contains(query, ignoreCase = true) ||
                    (event.statusCode?.toString()?.contains(query) == true)

            // Filter by status category
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

    val totalCount: StateFlow<Int> = combine(
        store?.getAll() ?: MutableStateFlow(emptyList())
    ) { eventsList ->
        eventsList.firstOrNull()?.size ?: 0
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: StatusFilter) {
        _activeFilter.value = filter
    }

    fun toggleSearch() {
        _isSearchVisible.value = !_isSearchVisible.value
        if (!_isSearchVisible.value) {
            _searchQuery.value = ""
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            store?.clear()
        }
    }
}
