package com.juanfbenitez.prueba.idealista.testutil

import com.juanfbenitez.prueba.idealista.data.api.IdealistaApi
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO

/** Manual test double for [IdealistaApi]: returns pre-set data or throws a pre-set error. */
class FakeIdealistaApi : IdealistaApi {

    var propertyList: List<PropertyDTO> = emptyList()
    var propertyDetail: PropertyDetailDTO? = null
    var propertyListError: Throwable? = null
    var propertyDetailError: Throwable? = null

    override suspend fun getPropertyList(): List<PropertyDTO> {
        propertyListError?.let { throw it }
        return propertyList
    }

    override suspend fun getPropertyDetail(): PropertyDetailDTO {
        propertyDetailError?.let { throw it }
        return propertyDetail ?: error("propertyDetail was not configured on FakeIdealistaApi")
    }
}
