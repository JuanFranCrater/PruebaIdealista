package com.juanfbenitez.prueba.idealista.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEntity::class], version = 2, exportSchema = false)
abstract class IdealistaDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
