package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository
import javax.inject.Inject

/** Checks whether a given property is currently marked as favorite. */
class IsFavoriteUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(propertyCode: String): Boolean = repository.isFavorite(propertyCode)
}
