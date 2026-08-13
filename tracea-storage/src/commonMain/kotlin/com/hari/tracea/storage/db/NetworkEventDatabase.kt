package com.hari.tracea.storage.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [NetworkEventEntity::class], version = 1, exportSchema = false)
@ConstructedBy(NetworkEventDatabaseConstructor::class)
abstract class NetworkEventDatabase : RoomDatabase() {
    abstract fun networkEventDao(): NetworkEventDao
}

expect fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<NetworkEventDatabase>

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NetworkEventDatabaseConstructor : RoomDatabaseConstructor<NetworkEventDatabase> {
    override fun initialize(): NetworkEventDatabase
}
