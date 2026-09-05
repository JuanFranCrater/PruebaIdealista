package com.juanfbenitez.prueba.idealista.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsFavoriteUseCaseTest {

    private lateinit var repository: FakePropertyRepository
    private lateinit var useCase: IsFavoriteUseCase

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
        useCase = IsFavoriteUseCase(repository)
    }

    @Test
    fun `when property code is favorited then returns true`() = runTest {
        repository.favoriteCodes = mutableSetOf("1")

        assertTrue(useCase("1"))
    }

    @Test
    fun `when property code is not favorited then returns false`() = runTest {
        repository.favoriteCodes = mutableSetOf("1")

        assertFalse(useCase("2"))
    }

    @Test
    fun `when invoked then forwards the property code to the repository`() = runTest {
        useCase("55")

        assertEquals("55", repository.lastIsFavoriteCheckedCode)
    }
}
