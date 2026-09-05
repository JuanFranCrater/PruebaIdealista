package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import com.juanfbenitez.prueba.idealista.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Manual test double for [PropertyRepository]. Records the arguments it was called with so tests
 * can assert use cases delegate correctly, and lets tests control what is returned.
 */
class FakePropertyRepository : PropertyRepository {

    var propertyList: List<Property> = emptyList()
    var propertyDetailByCode: Map<String, PropertyDetail> = emptyMap()
    val favoritesFlow = MutableStateFlow<List<Favorite>>(emptyList())
    var favoriteCodes: MutableSet<String> = mutableSetOf()

    var getPropertyListCallCount = 0
    var lastRequestedDetailCode: String? = null
    var lastIsFavoriteCheckedCode: String? = null
    var lastToggledFavoriteCode: String? = null

    override suspend fun getPropertyList(): List<Property> {
        getPropertyListCallCount++
        return propertyList
    }

    override suspend fun getPropertyDetail(propertyCode: String): PropertyDetail {
        lastRequestedDetailCode = propertyCode
        return propertyDetailByCode.getValue(propertyCode)
    }

    override fun getAllFavorites(): Flow<List<Favorite>> = favoritesFlow

    override suspend fun isFavorite(propertyCode: String): Boolean {
        lastIsFavoriteCheckedCode = propertyCode
        return favoriteCodes.contains(propertyCode)
    }

    override suspend fun toggleFavorite(propertyCode: String) {
        lastToggledFavoriteCode = propertyCode
        if (!favoriteCodes.add(propertyCode)) {
            favoriteCodes.remove(propertyCode)
        }
    }
}
