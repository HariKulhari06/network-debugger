package com.hari.networkdebugger.core.pipeline

import com.hari.networkdebugger.core.model.NetworkEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Collects and emits network events.
 */
interface NetworkEventCollector {
    suspend fun emit(event: NetworkEvent)
    fun update(id: String, updater: (NetworkEvent) -> NetworkEvent)
    val events: SharedFlow<NetworkEvent>
}

/**
 * Default implementation of NetworkEventCollector.
 */
class DefaultNetworkEventCollector : NetworkEventCollector {
    private val _events = MutableSharedFlow<NetworkEvent>(replay = 100, extraBufferCapacity = 100)
    override val events: SharedFlow<NetworkEvent> = _events.asSharedFlow()
    
    // Simplistic in-memory map to update events before they are finalized.
    private val ongoingEvents = mutableMapOf<String, NetworkEvent>()
    private val mutex = Mutex()

    override suspend fun emit(event: NetworkEvent) {
        mutex.withLock {
            ongoingEvents[event.id] = event
        }
        _events.emit(event)
    }

    override fun update(id: String, updater: (NetworkEvent) -> NetworkEvent) {
        // In a real application, you might want this to be suspend or handle concurrency differently.
        val current = ongoingEvents[id]
        if (current != null) {
            val updated = updater(current)
            ongoingEvents[id] = updated
            val success = _events.tryEmit(updated)
            if (!success) {
                // Handle emission failure if needed
            }
        }
    }
}
