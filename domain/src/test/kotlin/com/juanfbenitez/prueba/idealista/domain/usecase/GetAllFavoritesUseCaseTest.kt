package com.juanfbenitez.prueba.idealista.domain.usecase

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllFavoritesUseCaseTest {

    private lateinit var repository: FakePropertyRepository
    private lateinit var useCase: GetAllFavoritesUseCase

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
        useCase = GetAllFavoritesUseCase(repository)
    }

    @Test
    fun `when repository holds favorites then emits that list`() = runTest {
        val favorites = listOf(Favorite("1", 1000L), Favorite("2", 2000L))
        repository.favoritesFlow.value = favorites

        val result = useCase().first()

        assertEquals(favorites, result)
    }

    @Test
    fun `when repository flow changes then emits the updated list`() = runTest {
        repository.favoritesFlow.value = listOf(Favorite("1", 1000L))

        repository.favoritesFlow.value = listOf(Favorite("1", 1000L), Favorite("2", 2000L))
        val result = useCase().first()

        assertEquals(2, result.size)
    }
}
