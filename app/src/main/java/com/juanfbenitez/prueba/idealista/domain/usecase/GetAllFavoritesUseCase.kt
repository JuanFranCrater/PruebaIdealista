package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observes the current set of favorited properties. */
class GetAllFavoritesUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(): Flow<List<Favorite>> = repository.getAllFavorites()
}
