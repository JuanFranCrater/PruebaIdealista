package com.juanfbenitez.prueba.idealista.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class IdealistaDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: IdealistaDatabase? = null

        fun getDatabase(context: Context): IdealistaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IdealistaDatabase::class.java,
                    "idealista_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
