package com.juanfbenitez.prueba.idealista.data.repository

import com.juanfbenitez.prueba.idealista.data.api.model.PriceAmountDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PriceInfoDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import com.juanfbenitez.prueba.idealista.testutil.FakeFavoriteDao
import com.juanfbenitez.prueba.idealista.testutil.FakeIdealistaApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PropertyRepositoryImplTest {

    private lateinit var api: FakeIdealistaApi
    private lateinit var favoriteDao: FakeFavoriteDao
    private lateinit var repository: PropertyRepositoryImpl

    private fun propertyDTO(propertyCode: String = "1") = PropertyDTO(
        propertyCode = propertyCode,
        thumbnail = null,
        floor = "1",
        price = 100000.0,
        priceInfo = PriceInfoDTO(price = PriceAmountDTO(amount = 100000.0, currencySuffix = "€")),
        propertyType = "flat",
        operation = "sale",
        size = 70.0,
        exterior = true,
        rooms = 2,
        bathrooms = 1,
        address = "Main St",
        province = "Madrid",
        municipality = "Madrid",
        district = "Centro",
        country = "es",
        neighborhood = null,
        latitude = 40.0,
        longitude = -3.0,
        description = null,
        multimedia = null,
        features = null,
        parkingSpace = null
    )

    private fun propertyDetailDTO() = PropertyDetailDTO(
        adid = 1,
        price = 100000.0,
        priceInfo = PriceAmountDTO(amount = 100000.0, currencySuffix = "€"),
        operation = "sale",
        propertyType = "flat",
        extendedPropertyType = null,
        homeType = null,
        state = null,
        multimedia = null,
        propertyComment = null,
        ubication = null,
        country = null,
        moreCharacteristics = null,
        energyCertification = null
    )

    @Before
    fun setUp() {
        api = FakeIdealistaApi()
        favoriteDao = FakeFavoriteDao()
        repository = PropertyRepositoryImpl(api, favoriteDao)
    }

    @Test
    fun `when the api returns properties then getPropertyList maps them to domain models`() = runTest {
        api.propertyList = listOf(propertyDTO("1"), propertyDTO("2"))

        val result = repository.getPropertyList()

        assertEquals(listOf("1", "2"), result.map { it.propertyCode })
    }

    @Test
    fun `when the api returns a detail then getPropertyDetail maps it to a domain model`() = runTest {
        api.propertyDetail = propertyDetailDTO()

        val result = repository.getPropertyDetail("1")

        assertEquals("flat", result.propertyType)
    }

    @Test
    fun `when the dao has no favorites then getAllFavorites emits an empty list`() = runTest {
        val result = repository.getAllFavorites().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when the dao has favorites then getAllFavorites emits them mapped to domain models`() = runTest {
        favoriteDao.insertFavorite(FavoriteEntity("1", dateFavorited = 111L))

        val result = repository.getAllFavorites().first()

        assertEquals(listOf("1"), result.map { it.propertyCode })
        assertEquals(111L, result.first().dateFavorited)
    }

    @Test
    fun `when a property is not favorited then isFavorite returns false`() = runTest {
        val result = repository.isFavorite("1")

        assertFalse(result)
    }

    @Test
    fun `when a property is favorited then isFavorite returns true`() = runTest {
        favoriteDao.insertFavorite(FavoriteEntity("1", dateFavorited = 111L))

        val result = repository.isFavorite("1")

        assertTrue(result)
    }

    @Test
    fun `when toggling a non-favorited property then it becomes favorited`() = runTest {
        repository.toggleFavorite("1")

        assertTrue(repository.isFavorite("1"))
    }

    @Test
    fun `when toggling an already favorited property then it is removed`() = runTest {
        favoriteDao.insertFavorite(FavoriteEntity("1", dateFavorited = 111L))

        repository.toggleFavorite("1")

        assertFalse(repository.isFavorite("1"))
    }

    @Test
    fun `when toggling a favorite then it is stored as favorite with a recorded timestamp`() = runTest {
        val before = System.currentTimeMillis()

        repository.toggleFavorite("1")

        val stored = favoriteDao.getFavoriteById("1")
        assertEquals("1", stored?.propertyCode)
        assertTrue((stored?.dateFavorited ?: 0L) >= before)
    }
}
