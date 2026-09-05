package com.juanfbenitez.prueba.idealista.di

import android.content.Context
import androidx.room.Room
import com.juanfbenitez.prueba.idealista.data.db.FavoriteDao
import com.juanfbenitez.prueba.idealista.data.db.IdealistaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides the Room database and its DAOs as app-wide singletons. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideIdealistaDatabase(@ApplicationContext context: Context): IdealistaDatabase {
        return Room.databaseBuilder(
            context,
            IdealistaDatabase::class.java,
            "idealista_database"
        ).build()
    }

    @Provides
    fun provideFavoriteDao(database: IdealistaDatabase): FavoriteDao =
        database.favoriteDao()
}
