package com.juanfbenitez.prueba.idealista.testutil

import com.juanfbenitez.prueba.idealista.data.db.FavoriteDao
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory test double for [FavoriteDao], backed by a map keyed by property code. */
class FakeFavoriteDao : FavoriteDao {

    private val favoritesByCode = linkedMapOf<String, FavoriteEntity>()
    private val favoritesFlow = MutableStateFlow<List<FavoriteEntity>>(emptyList())

    private fun publish() {
        favoritesFlow.value = favoritesByCode.values.toList()
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoritesFlow

    override suspend fun getFavoriteById(propertyCode: String): FavoriteEntity? =
        favoritesByCode[propertyCode]

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoritesByCode[favorite.propertyCode] = favorite
        publish()
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favoritesByCode.remove(favorite.propertyCode)
        publish()
    }

    override suspend fun deleteFavoriteById(propertyCode: String) {
        favoritesByCode.remove(propertyCode)
        publish()
    }
}
