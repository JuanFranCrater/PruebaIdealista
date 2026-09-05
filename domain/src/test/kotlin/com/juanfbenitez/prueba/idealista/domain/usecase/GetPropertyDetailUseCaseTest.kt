package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.domain.model.Price
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPropertyDetailUseCaseTest {

    private lateinit var repository: FakePropertyRepository
    private lateinit var useCase: GetPropertyDetailUseCase

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
        useCase = GetPropertyDetailUseCase(repository)
    }

    @Test
    fun `when property code matches then returns its detail`() = runTest {
        val detail = PropertyDetail(
            price = Price(250000.0, "€"),
            propertyType = "flat",
            description = "Nice place",
            images = listOf("https://example.com/1.jpg"),
            characteristics = null
        )
        repository.propertyDetailByCode = mapOf("42" to detail)

        val result = useCase("42")

        assertEquals(detail, result)
    }

    @Test
    fun `when invoked then forwards the property code to the repository`() = runTest {
        val detail = PropertyDetail(
            price = Price(1.0, "€"),
            propertyType = "flat",
            description = null,
            images = emptyList(),
            characteristics = null
        )
        repository.propertyDetailByCode = mapOf("99" to detail)

        useCase("99")

        assertEquals("99", repository.lastRequestedDetailCode)
    }
}
