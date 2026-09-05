package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.domain.model.Price
import com.juanfbenitez.prueba.idealista.domain.model.Property
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPropertyListUseCaseTest {

    private lateinit var repository: FakePropertyRepository
    private lateinit var useCase: GetPropertyListUseCase

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
        useCase = GetPropertyListUseCase(repository)
    }

    @Test
    fun `when repository has properties then returns that list`() = runTest {
        val properties = listOf(
            Property(
                propertyCode = "1",
                thumbnail = null,
                price = Price(100000.0, "€"),
                operation = "sale",
                propertyType = "flat",
                size = 80.0,
                rooms = 2,
                bathrooms = 1,
                address = "Main St"
            )
        )
        repository.propertyList = properties

        val result = useCase()

        assertEquals(properties, result)
    }

    @Test
    fun `when invoked twice then calls the repository twice`() = runTest {
        useCase()
        useCase()

        assertEquals(2, repository.getPropertyListCallCount)
    }

    @Test
    fun `when repository has no properties then returns an empty list`() = runTest {
        repository.propertyList = emptyList()

        val result = useCase()

        assertEquals(emptyList<Property>(), result)
    }
}
