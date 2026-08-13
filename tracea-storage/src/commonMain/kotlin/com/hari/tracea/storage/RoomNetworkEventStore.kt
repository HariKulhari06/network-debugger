package com.hari.tracea.storage

import com.hari.tracea.core.config.StorageConfig
import com.hari.tracea.core.model.BodyContentType
import com.hari.tracea.core.model.BodyData
import com.hari.tracea.core.model.NetworkEvent
import com.hari.tracea.core.store.NetworkEventStore
import com.hari.tracea.storage.body.BodyFileStorage
import com.hari.tracea.storage.db.NetworkEventDatabase
import com.hari.tracea.storage.db.NetworkEventEntity
import com.hari.tracea.storage.db.getDatabaseBuilder
import com.hari.tracea.storage.mapper.EntityMapper.toDomain
import com.hari.tracea.storage.mapper.EntityMapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomNetworkEventStore(
    private val context: Any?,
    private val storageConfig: StorageConfig = StorageConfig()
) : NetworkEventStore {

    private val database by lazy { getDatabaseBuilder(context).fallbackToDestructiveMigration(true).build() }
    private val dao by lazy { database.networkEventDao() }
    private val bodyStorage by lazy { BodyFileStorage(context) }

    override suspend fun insert(event: NetworkEvent): Unit = withContext(Dispatchers.Default) {
        val reqContent = (event.requestBody as? BodyData.Text)?.content
        val resContent = (event.responseBody as? BodyData.Text)?.content

        val reqRef = reqContent?.let { bodyStorage.writeBody(event.id, true, it) }
        val resRef = resContent?.let { bodyStorage.writeBody(event.id, false, it) }

        val entity = event.toEntity(reqRef, resRef)
        dao.insert(entity)

        enforceRetention()
    }

    override suspend fun update(event: NetworkEvent): Unit = withContext(Dispatchers.Default) {
        val existing = dao.getById(event.id)
        
        val reqContent = (event.requestBody as? BodyData.Text)?.content
        val resContent = (event.responseBody as? BodyData.Text)?.content

        val reqRef = reqContent?.let { 
            bodyStorage.writeBody(event.id, true, it) 
        } ?: existing?.requestBodyRef

        val resRef = resContent?.let { 
            bodyStorage.writeBody(event.id, false, it) 
        } ?: existing?.responseBodyRef

        val entity = event.toEntity(reqRef, resRef)
        dao.insert(entity)
    }

    override suspend fun get(id: String): NetworkEvent? = withContext(Dispatchers.Default) {
        dao.getById(id)?.let { mapEntityToDomain(it) }
    }

    override fun getAll(): Flow<List<NetworkEvent>> {
        return dao.getAll().map { list ->
            list.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun search(query: String): List<NetworkEvent> = withContext(Dispatchers.Default) {
        dao.search(query).map { mapEntityToDomain(it) }
    }

    override suspend fun clear(): Unit = withContext(Dispatchers.Default) {
        dao.deleteAll()
        bodyStorage.deleteAllBodies()
    }

    override suspend fun delete(id: String): Unit = withContext(Dispatchers.Default) {
        val existing = dao.getById(id)
        if (existing != null) {
            existing.requestBodyRef?.let { bodyStorage.deleteBody(it) }
            existing.responseBodyRef?.let { bodyStorage.deleteBody(it) }
            dao.deleteById(id)
        }
    }

    override suspend fun getCount(): Int = withContext(Dispatchers.Default) {
        dao.getCount()
    }

    override suspend fun deleteSession(sessionId: String): Unit = withContext(Dispatchers.Default) {
        val ids = dao.getIdsBySessionId(sessionId)
        for (id in ids) {
            val entity = dao.getById(id)
            entity?.requestBodyRef?.let { bodyStorage.deleteBody(it) }
            entity?.responseBodyRef?.let { bodyStorage.deleteBody(it) }
        }
        dao.deleteBySessionId(sessionId)
    }

    override suspend fun getSessionEvents(sessionId: String): List<NetworkEvent> = withContext(Dispatchers.Default) {
        dao.getEventsBySessionId(sessionId).map { mapEntityToDomain(it) }
    }

    private suspend fun enforceRetention() {
        val distinctSessions = dao.getDistinctSessionIds()
        if (distinctSessions.size > 5) {
            val sessionsToDelete = distinctSessions.drop(5)
            for (sessionId in sessionsToDelete) {
                val ids = dao.getIdsBySessionId(sessionId)
                for (id in ids) {
                    val entity = dao.getById(id)
                    entity?.requestBodyRef?.let { bodyStorage.deleteBody(it) }
                    entity?.responseBodyRef?.let { bodyStorage.deleteBody(it) }
                }
                dao.deleteBySessionId(sessionId)
            }
        }
    }

    private fun mapEntityToDomain(entity: NetworkEventEntity): NetworkEvent {
        val reqContent = entity.requestBodyRef?.let { bodyStorage.readBody(it) }
        val resContent = entity.responseBodyRef?.let { bodyStorage.readBody(it) }

        val reqContentType = entity.requestBodyType?.let { runCatching { BodyContentType.valueOf(it) }.getOrNull() } ?: BodyContentType.UNKNOWN
        val reqSize = entity.requestBodySize ?: reqContent?.encodeToByteArray()?.size?.toLong() ?: 0L
        val reqBody = reqContent?.let { BodyData.Text(content = it, contentType = reqContentType, size = reqSize) }

        val resContentType = entity.responseBodyType?.let { runCatching { BodyContentType.valueOf(it) }.getOrNull() } ?: BodyContentType.UNKNOWN
        val resSize = entity.responseBodySize ?: resContent?.encodeToByteArray()?.size?.toLong() ?: 0L
        val resBody = resContent?.let { BodyData.Text(content = it, contentType = resContentType, size = resSize) }

        return entity.toDomain(reqBody, resBody)
    }
}
