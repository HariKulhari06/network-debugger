package com.hari.networkdebugger.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NetworkEventEntity::class], version = 1, exportSchema = false)
internal abstract class NetworkEventDatabase : RoomDatabase() {
    abstract fun networkEventDao(): NetworkEventDao

    companion object {
        @Volatile
        private var INSTANCE: NetworkEventDatabase? = null

        fun getInstance(context: Context): NetworkEventDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NetworkEventDatabase::class.java,
                    "network_debugger_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
