package com.juanfbenitez.prueba.idealista.data.repository

import com.juanfbenitez.prueba.idealista.data.api.IdealistaApi
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import com.juanfbenitez.prueba.idealista.data.db.FavoriteDao
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class PropertyRepository(
    private val api: IdealistaApi,
    private val favoriteDao: FavoriteDao
) {
    suspend fun getPropertyList(): List<PropertyDTO> {
        return api.getPropertyList()
    }

    suspend fun getPropertyDetail(propertyCode: String): PropertyDetailDTO {
        // Note: The API endpoint provided is static, but in a real app it would likely take an ID.
        // For this challenge, we just call the static detail endpoint.
        return api.getPropertyDetail()
    }

    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun isFavorite(propertyCode: String): Boolean {
        return favoriteDao.getFavoriteById(propertyCode) != null
    }

    suspend fun toggleFavorite(propertyCode: String) {
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
