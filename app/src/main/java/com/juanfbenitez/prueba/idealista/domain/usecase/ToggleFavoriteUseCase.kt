package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository
import javax.inject.Inject

/** Toggles the favorite status of a property. */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(propertyCode: String) = repository.toggleFavorite(propertyCode)
}
