package com.hari.tracea.storage.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<NetworkEventDatabase> {
    val dbFilePath = NSHomeDirectory() + "/Documents/tracea_db"
    return Room.databaseBuilder<NetworkEventDatabase>(
        name = dbFilePath,
        factory = { NetworkEventDatabaseConstructor.initialize() }
    )
}
