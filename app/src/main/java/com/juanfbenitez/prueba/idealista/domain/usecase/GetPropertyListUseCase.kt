package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.repository.PropertyRepository
import javax.inject.Inject

/** Fetches the full property list, independent of operation (sale/rent). */
class GetPropertyListUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(): List<PropertyDTO> = repository.getPropertyList()
}
