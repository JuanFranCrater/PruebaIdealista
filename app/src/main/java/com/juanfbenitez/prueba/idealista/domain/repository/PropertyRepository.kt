package com.juanfbenitez.prueba.idealista.domain.repository

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction the domain layer (use cases) depends on to access property data, without knowing
 * whether it comes from the network, a local database, or any other source. Implemented in the
 * `data` layer by [com.juanfbenitez.prueba.idealista.data.repository.PropertyRepositoryImpl].
 */
interface PropertyRepository {
    suspend fun getPropertyList(): List<Property>

    suspend fun getPropertyDetail(propertyCode: String): PropertyDetail

    fun getAllFavorites(): Flow<List<Favorite>>

    suspend fun isFavorite(propertyCode: String): Boolean

    suspend fun toggleFavorite(propertyCode: String)
}
