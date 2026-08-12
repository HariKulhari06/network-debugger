package com.hari.networkdebugger.kmp

import com.hari.networkdebugger.kmp.model.NetworkEventKmp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NetworkDebuggerKmp {

    private val _events = MutableStateFlow<List<NetworkEventKmp>>(emptyList())
    val events: StateFlow<List<NetworkEventKmp>> = _events.asStateFlow()

    private var maxHistorySize: Int = 500
    private var isEnabled: Boolean = true

    fun configure(enabled: Boolean = true, maxHistorySize: Int = 500) {
        this.isEnabled = enabled
        this.maxHistorySize = maxHistorySize
    }

    fun logEvent(event: NetworkEventKmp) {
        if (!isEnabled) return
        val currentList = _events.value.toMutableList()
        currentList.add(0, event)
        if (currentList.size > maxHistorySize) {
            currentList.removeAt(currentList.lastIndex)
        }
        _events.value = currentList
    }

    fun clearHistory() {
        _events.value = emptyList()
    }
}
