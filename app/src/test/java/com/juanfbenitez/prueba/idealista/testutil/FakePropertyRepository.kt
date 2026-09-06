package com.juanfbenitez.prueba.idealista.testutil

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import com.juanfbenitez.prueba.idealista.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Manual test double for [PropertyRepository], shared by app-module test suites (ViewModels,
 * repository) that need to exercise the real use cases without a mocking framework.
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
    var propertyListError: Throwable? = null
    var propertyDetailError: Throwable? = null

    override suspend fun getPropertyList(): List<Property> {
        getPropertyListCallCount++
        propertyListError?.let { throw it }
        return propertyList
    }

    override suspend fun getPropertyDetail(propertyCode: String): PropertyDetail {
        lastRequestedDetailCode = propertyCode
        propertyDetailError?.let { throw it }
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
