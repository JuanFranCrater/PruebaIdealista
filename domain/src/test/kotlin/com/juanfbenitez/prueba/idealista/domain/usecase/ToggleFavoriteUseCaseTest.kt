package com.juanfbenitez.prueba.idealista.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: FakePropertyRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `when invoked then forwards the property code to the repository`() = runTest {
        useCase("7")

        assertEquals("7", repository.lastToggledFavoriteCode)
    }

    @Test
    fun `when property is not favorited then marks it as favorite`() = runTest {
        useCase("7")

        assertTrue(repository.favoriteCodes.contains("7"))
    }

    @Test
    fun `when property is already favorited then unmarks it`() = runTest {
        repository.favoriteCodes = mutableSetOf("7")

        useCase("7")

        assertEquals(false, repository.favoriteCodes.contains("7"))
    }
}
