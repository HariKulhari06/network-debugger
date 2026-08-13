package com.hari.tracea.storage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: NetworkEventEntity)

    @Update
    suspend fun update(event: NetworkEventEntity)

    @Query("SELECT * FROM network_events WHERE id = :id")
    suspend fun getById(id: String): NetworkEventEntity?

    @Query("SELECT * FROM network_events ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NetworkEventEntity>>

    @Query("SELECT * FROM network_events WHERE url LIKE '%' || :query || '%' OR host LIKE '%' || :query || '%' OR method LIKE '%' || :query || '%' OR CAST(statusCode AS TEXT) LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun search(query: String): List<NetworkEventEntity>

    @Query("SELECT COUNT(*) FROM network_events")
    suspend fun getCount(): Int

    @Query("DELETE FROM network_events")
    suspend fun deleteAll()

    @Query("DELETE FROM network_events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM network_events WHERE id IN (SELECT id FROM network_events ORDER BY timestamp ASC LIMIT :count)")
    suspend fun deleteOldest(count: Int)

    @Query("SELECT id FROM network_events ORDER BY timestamp ASC LIMIT :count")
    suspend fun getOldestIds(count: Int): List<String>

    @Query("SELECT DISTINCT sessionId FROM network_events ORDER BY timestamp DESC")
    suspend fun getDistinctSessionIds(): List<String>

    @Query("DELETE FROM network_events WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: String)

    @Query("SELECT id FROM network_events WHERE sessionId = :sessionId")
    suspend fun getIdsBySessionId(sessionId: String): List<String>

    @Query("SELECT * FROM network_events WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    suspend fun getEventsBySessionId(sessionId: String): List<NetworkEventEntity>
}
