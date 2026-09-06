package com.juanfbenitez.prueba.idealista.ui.list

import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Price
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.usecase.GetAllFavoritesUseCase
import com.juanfbenitez.prueba.idealista.domain.usecase.GetPropertyListUseCase
import com.juanfbenitez.prueba.idealista.domain.usecase.ToggleFavoriteUseCase
import com.juanfbenitez.prueba.idealista.testutil.FakePropertyRepository
import com.juanfbenitez.prueba.idealista.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PropertyListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakePropertyRepository
    private lateinit var viewModel: PropertyListViewModel

    private fun property(code: String, operation: String) = Property(
        propertyCode = code,
        thumbnail = null,
        price = Price(100000.0, "€"),
        operation = operation,
        propertyType = "flat",
        size = 80.0,
        rooms = 2,
        bathrooms = 1,
        address = "Main St"
    )

    private fun createViewModel() {
        viewModel = PropertyListViewModel(
            getPropertyListUseCase = GetPropertyListUseCase(repository),
            getAllFavoritesUseCase = GetAllFavoritesUseCase(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
        )
    }

    @Before
    fun setUp() {
        repository = FakePropertyRepository()
    }

    @Test
    fun `when operation is sale then observeProperties returns only sale properties`() = runTest {
        repository.propertyList = listOf(property("1", "sale"), property("2", "rent"))
        createViewModel()

        val state = viewModel.observeProperties(PropertyOperation.SALE).first { it is PropertyListUiState.Success }

        val properties = (state as PropertyListUiState.Success).properties
        assertEquals(1, properties.size)
        assertEquals("1", properties[0].property.propertyCode)
    }

    @Test
    fun `when operation is rent then observeProperties returns only rent properties`() = runTest {
        repository.propertyList = listOf(property("1", "sale"), property("2", "rent"))
        createViewModel()

        val state = viewModel.observeProperties(PropertyOperation.RENT).first { it is PropertyListUiState.Success }

        val properties = (state as PropertyListUiState.Success).properties
        assertEquals(1, properties.size)
        assertEquals("2", properties[0].property.propertyCode)
    }

    @Test
    fun `when operation is favorites then observeProperties returns favorited properties regardless of operation`() = runTest {
        repository.propertyList = listOf(property("1", "sale"), property("2", "rent"), property("3", "sale"))
        repository.favoritesFlow.value = listOf(Favorite("1", 1000L), Favorite("2", 2000L))
        createViewModel()

        val state = viewModel.observeProperties(PropertyOperation.FAVORITES).first { it is PropertyListUiState.Success }

        val properties = (state as PropertyListUiState.Success).properties
        assertEquals(setOf("1", "2"), properties.map { it.property.propertyCode }.toSet())
    }

    @Test
    fun `when a property is favorited then its ui model carries isFavorite and dateFavorited`() = runTest {
        repository.propertyList = listOf(property("1", "sale"))
        repository.favoritesFlow.value = listOf(Favorite("1", 5555L))
        createViewModel()

        val state = viewModel.observeProperties(PropertyOperation.SALE).first { it is PropertyListUiState.Success }

        val item = (state as PropertyListUiState.Success).properties.single()
        assertTrue(item.isFavorite)
        assertEquals(5555L, item.dateFavorited)
    }

    @Test
    fun `when a property is not favorited then its ui model has isFavorite false and no date`() = runTest {
        repository.propertyList = listOf(property("1", "sale"))
        createViewModel()

        val state = viewModel.observeProperties(PropertyOperation.SALE).first { it is PropertyListUiState.Success }

        val item = (state as PropertyListUiState.Success).properties.single()
        assertEquals(false, item.isFavorite)
        assertNull(item.dateFavorited)
    }

    @Test
    fun `when loading the property list fails then observeProperties emits an error state`() = runTest {
        repository.propertyListError = RuntimeException("network down")
        createViewModel()

        val state = viewModel.observeProperties(PropertyOperation.SALE).first { it is PropertyListUiState.Error }

        assertEquals("network down", (state as PropertyListUiState.Error).message)
    }

    @Test
    fun `when toggleFavorite is called then it forwards the property code to the use case`() = runTest {
        createViewModel()

        viewModel.toggleFavorite("42")

        assertEquals("42", repository.lastToggledFavoriteCode)
    }

    @Test
    fun `when selectOperation is called then selectedOperation reflects the new value`() = runTest {
        createViewModel()

        viewModel.selectOperation(PropertyOperation.RENT)

        assertEquals(PropertyOperation.RENT, viewModel.selectedOperation.value)
    }

    @Test
    fun `when selectPropertyForDrawer is called then selectedPropertyForDrawer holds that code`() = runTest {
        createViewModel()

        viewModel.selectPropertyForDrawer("7")

        assertEquals("7", viewModel.selectedPropertyForDrawer.value)
    }

    @Test
    fun `when clearDrawerSelection is called then selectedPropertyForDrawer becomes null`() = runTest {
        createViewModel()
        viewModel.selectPropertyForDrawer("7")

        viewModel.clearDrawerSelection()

        assertNull(viewModel.selectedPropertyForDrawer.value)
    }
}
