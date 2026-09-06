package com.juanfbenitez.prueba.idealista.ui.detail

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Price
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail
import com.juanfbenitez.prueba.idealista.domain.usecase.GetAllFavoritesUseCase
import com.juanfbenitez.prueba.idealista.domain.usecase.GetPropertyDetailUseCase
import com.juanfbenitez.prueba.idealista.domain.usecase.ToggleFavoriteUseCase
import com.juanfbenitez.prueba.idealista.testutil.FakePropertyRepository
import com.juanfbenitez.prueba.idealista.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PropertyDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakePropertyRepository

    private val detail = PropertyDetail(
        price = Price(200000.0, "€"),
        propertyType = "flat",
        description = "A nice flat",
        images = emptyList(),
        characteristics = null
    )

    private fun createViewModel(propertyCode: String = "1") = PropertyDetailViewModel(
        getPropertyDetailUseCase = GetPropertyDetailUseCase(repository),
        getAllFavoritesUseCase = GetAllFavoritesUseCase(repository),
        toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
        propertyCode = propertyCode
    )

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
    }

    @Test
    fun `when detail loads successfully then uiState becomes Success with the detail`() = runTest {
        repository.propertyDetailByCode = mapOf("1" to detail)

        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it is PropertyDetailUiState.Success }

        assertEquals(detail, (state as PropertyDetailUiState.Success).detail)
    }

    @Test
    fun `when the property is favorited then uiState Success carries the matching favorite`() = runTest {
        repository.propertyDetailByCode = mapOf("1" to detail)
        repository.favoritesFlow.value = listOf(Favorite("1", 4242L))

        val viewModel = createViewModel(propertyCode = "1")
        val state = viewModel.uiState.first { it is PropertyDetailUiState.Success }

        assertEquals(Favorite("1", 4242L), (state as PropertyDetailUiState.Success).favorite)
    }

    @Test
    fun `when the property is not favorited then uiState Success carries a null favorite`() = runTest {
        repository.propertyDetailByCode = mapOf("1" to detail)
        repository.favoritesFlow.value = listOf(Favorite("other", 1L))

        val viewModel = createViewModel(propertyCode = "1")
        val state = viewModel.uiState.first { it is PropertyDetailUiState.Success }

        assertNull((state as PropertyDetailUiState.Success).favorite)
    }

    @Test
    fun `when loading the detail fails then uiState becomes Error with the failure message`() = runTest {
        repository.propertyDetailError = RuntimeException("not found")

        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it is PropertyDetailUiState.Error }

        assertEquals("not found", (state as PropertyDetailUiState.Error).message)
    }

    @Test
    fun `when created then requests the detail for the assisted property code`() = runTest {
        repository.propertyDetailByCode = mapOf("55" to detail)

        val viewModel = createViewModel(propertyCode = "55")
        viewModel.uiState.first { it is PropertyDetailUiState.Success }

        assertEquals("55", repository.lastRequestedDetailCode)
    }

    @Test
    fun `when toggleFavorite is called then it forwards the assisted property code to the use case`() = runTest {
        repository.propertyDetailByCode = mapOf("1" to detail)
        val viewModel = createViewModel(propertyCode = "1")

        viewModel.toggleFavorite()

        assertEquals("1", repository.lastToggledFavoriteCode)
    }

    @Test
    fun `when loadDetail is called again then uiState transitions back through Loading`() = runTest {
        repository.propertyDetailByCode = mapOf("1" to detail)
        val viewModel = createViewModel(propertyCode = "1")
        viewModel.uiState.first { it is PropertyDetailUiState.Success }

        repository.propertyDetailError = RuntimeException("boom")
        viewModel.loadDetail()

        val state = viewModel.uiState.first { it is PropertyDetailUiState.Error }
        assertEquals("boom", (state as PropertyDetailUiState.Error).message)
    }
}
