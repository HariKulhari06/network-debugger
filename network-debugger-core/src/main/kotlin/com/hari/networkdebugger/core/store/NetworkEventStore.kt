package com.hari.networkdebugger.core.store

import com.hari.networkdebugger.core.model.NetworkEvent
import kotlinx.coroutines.flow.Flow

/**
 * Persistent store for network events.
 */
interface NetworkEventStore {
    suspend fun insert(event: NetworkEvent)
    suspend fun update(event: NetworkEvent)
    suspend fun get(id: String): NetworkEvent?
    fun getAll(): Flow<List<NetworkEvent>>
    suspend fun search(query: String): List<NetworkEvent>
    suspend fun clear()
    suspend fun delete(id: String)
    suspend fun getCount(): Int
}
