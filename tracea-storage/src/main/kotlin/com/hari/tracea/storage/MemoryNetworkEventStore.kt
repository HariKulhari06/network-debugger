package com.hari.tracea.storage

import com.hari.tracea.core.model.NetworkEvent
import com.hari.tracea.core.store.NetworkEventStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MemoryNetworkEventStore : NetworkEventStore {
    private val eventsFlow = MutableStateFlow<List<NetworkEvent>>(emptyList())
    
    override suspend fun insert(event: NetworkEvent) {
        eventsFlow.update { current ->
            val mutableList = current.toMutableList()
            val existingIdx = mutableList.indexOfFirst { it.id == event.id }
            if (existingIdx != -1) {
                mutableList[existingIdx] = event
            } else {
                mutableList.add(0, event)
            }
            mutableList
        }
    }

    override suspend fun update(event: NetworkEvent) {
        insert(event)
    }

    override suspend fun get(id: String): NetworkEvent? {
        return eventsFlow.value.find { it.id == id }
    }

    override fun getAll(): Flow<List<NetworkEvent>> {
        return eventsFlow
    }

    override suspend fun search(query: String): List<NetworkEvent> {
        val q = query.lowercase()
        return eventsFlow.value.filter {
            it.url.lowercase().contains(q) ||
            it.host.lowercase().contains(q) ||
            it.method.name.lowercase().contains(q) ||
            it.statusCode?.toString()?.contains(q) == true
        }
    }

    override suspend fun clear() {
        eventsFlow.value = emptyList()
    }

    override suspend fun delete(id: String) {
        eventsFlow.update { current ->
            current.filter { it.id != id }
        }
    }

    override suspend fun getCount(): Int {
        return eventsFlow.value.size
    }

    override suspend fun deleteSession(sessionId: String) {
        eventsFlow.update { current ->
            current.filter { it.sessionId != sessionId }
        }
    }

    override suspend fun getSessionEvents(sessionId: String): List<NetworkEvent> {
        return eventsFlow.value.filter { it.sessionId == sessionId }
    }
}
