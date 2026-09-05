package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import com.juanfbenitez.prueba.idealista.domain.repository.PropertyRepository
import javax.inject.Inject

/** Fetches the detail for a single property. */
class GetPropertyDetailUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(propertyCode: String): PropertyDetail =
        repository.getPropertyDetail(propertyCode)
}
