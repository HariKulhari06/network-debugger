package com.hari.tracea.storage.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<NetworkEventDatabase> {
    val appContext = (context as Context).applicationContext
    val dbFile = appContext.getDatabasePath("tracea_db")
    return Room.databaseBuilder<NetworkEventDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
