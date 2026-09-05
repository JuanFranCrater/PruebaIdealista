package com.juanfbenitez.prueba.idealista.data.repository

import com.juanfbenitez.prueba.idealista.data.api.IdealistaApi
import com.juanfbenitez.prueba.idealista.data.db.FavoriteDao
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import com.juanfbenitez.prueba.idealista.data.mapper.toDomain
import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import com.juanfbenitez.prueba.idealista.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [PropertyRepository] implementation combining the remote API and the local favorites database,
 * mapping their data-layer models (DTOs/entities) to domain models so callers never see them.
 */
class PropertyRepositoryImpl @Inject constructor(
    private val api: IdealistaApi,
    private val favoriteDao: FavoriteDao
) : PropertyRepository {

    override suspend fun getPropertyList(): List<Property> {
        return api.getPropertyList().map { it.toDomain() }
    }

    override suspend fun getPropertyDetail(propertyCode: String): PropertyDetail {
        // Note: The API endpoint provided is static, but in a real app it would likely take an ID.
        // For this challenge, we just call the static detail endpoint.
        return api.getPropertyDetail().toDomain()
    }

    override fun getAllFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getAllFavorites().map { favorites -> favorites.map { it.toDomain() } }
    }

    override suspend fun isFavorite(propertyCode: String): Boolean {
        return favoriteDao.getFavoriteById(propertyCode) != null
    }

    override suspend fun toggleFavorite(propertyCode: String) {
        val existing = favoriteDao.getFavoriteById(propertyCode)
        if (existing != null) {
            favoriteDao.deleteFavoriteById(propertyCode)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    propertyCode = propertyCode,
                    isFavorite = true,
                    dateFavorited = System.currentTimeMillis()
                )
            )
        }
    }
}
