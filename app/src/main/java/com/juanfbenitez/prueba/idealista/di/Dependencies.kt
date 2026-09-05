package com.juanfbenitez.prueba.idealista.di

import android.content.Context
import com.juanfbenitez.prueba.idealista.data.api.IdealistaApi
import com.juanfbenitez.prueba.idealista.data.db.IdealistaDatabase
import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Dependencies {

    private var propertyRepository: PropertyRepository? = null

    fun providePropertyRepository(context: Context): PropertyRepository {
        return propertyRepository ?: synchronized(this) {
            propertyRepository ?: createRepository(context).also { propertyRepository = it }
        }
    }

    private fun createRepository(context: Context): PropertyRepository {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(IdealistaApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(IdealistaApi::class.java)
        val database = IdealistaDatabase.getDatabase(context)
        
        return PropertyRepository(api, database.favoriteDao())
    }
}
