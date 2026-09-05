package com.juanfbenitez.prueba.idealista.data.api

import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import retrofit2.http.GET

interface IdealistaApi {

    @GET("list.json")
    suspend fun getPropertyList(): List<PropertyDTO>

    @GET("detail.json")
    suspend fun getPropertyDetail(): PropertyDetailDTO

    companion object {
        const val BASE_URL = "https://idealista.github.io/android-challenge/"
    }
}
