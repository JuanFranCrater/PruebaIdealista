package com.juanfbenitez.prueba.idealista.data.mapper

import com.juanfbenitez.prueba.idealista.data.api.model.ImageDTO
import com.juanfbenitez.prueba.idealista.data.api.model.MoreCharacteristicsDTO
import com.juanfbenitez.prueba.idealista.data.api.model.MultimediaDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PriceAmountDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PriceInfoDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PropertyMappersTest {

    private fun propertyDTO(
        propertyCode: String = "1",
        thumbnail: String? = "https://example.com/thumb.jpg",
        amount: Double = 150000.0,
        currencySuffix: String = "€",
        operation: String = "sale",
        propertyType: String = "flat",
        size: Double = 90.0,
        rooms: Int = 3,
        bathrooms: Int = 2,
        address: String = "Calle Mayor 1"
    ) = PropertyDTO(
        propertyCode = propertyCode,
        thumbnail = thumbnail,
        floor = "2",
        price = amount,
        priceInfo = PriceInfoDTO(price = PriceAmountDTO(amount = amount, currencySuffix = currencySuffix)),
        propertyType = propertyType,
        operation = operation,
        size = size,
        exterior = true,
        rooms = rooms,
        bathrooms = bathrooms,
        address = address,
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

    private fun propertyDetailDTO(
        amount: Double = 250000.0,
        currencySuffix: String = "€",
        propertyType: String = "flat",
        propertyComment: String? = "A lovely place",
        multimedia: MultimediaDTO? = null,
        moreCharacteristics: MoreCharacteristicsDTO? = null
    ) = PropertyDetailDTO(
        adid = 42,
        price = amount,
        priceInfo = PriceAmountDTO(amount = amount, currencySuffix = currencySuffix),
        operation = "sale",
        propertyType = propertyType,
        extendedPropertyType = null,
        homeType = null,
        state = null,
        multimedia = multimedia,
        propertyComment = propertyComment,
        ubication = null,
        country = null,
        moreCharacteristics = moreCharacteristics,
        energyCertification = null
    )

    @Test
    fun `when mapping a PropertyDTO then returns a Property with the same fields`() {
        val dto = propertyDTO()

        val result = dto.toDomain()

        assertEquals(dto.propertyCode, result.propertyCode)
        assertEquals(dto.thumbnail, result.thumbnail)
        assertEquals(dto.priceInfo.price.amount, result.price.amount, 0.0)
        assertEquals(dto.priceInfo.price.currencySuffix, result.price.currencySuffix)
        assertEquals(dto.operation, result.operation)
        assertEquals(dto.propertyType, result.propertyType)
        assertEquals(dto.size, result.size, 0.0)
        assertEquals(dto.rooms, result.rooms)
        assertEquals(dto.bathrooms, result.bathrooms)
        assertEquals(dto.address, result.address)
    }

    @Test
    fun `when PropertyDTO has a null thumbnail then Property thumbnail is also null`() {
        val dto = propertyDTO(thumbnail = null)

        val result = dto.toDomain()

        assertNull(result.thumbnail)
    }

    @Test
    fun `when mapping a PropertyDetailDTO then returns a PropertyDetail with the same fields`() {
        val dto = propertyDetailDTO()

        val result = dto.toDomain()

        assertEquals(dto.priceInfo.amount, result.price.amount, 0.0)
        assertEquals(dto.priceInfo.currencySuffix, result.price.currencySuffix)
        assertEquals(dto.propertyType, result.propertyType)
        assertEquals(dto.propertyComment, result.description)
    }

    @Test
    fun `when PropertyDetailDTO has no multimedia then images is an empty list`() {
        val dto = propertyDetailDTO(multimedia = null)

        val result = dto.toDomain()

        assertTrue(result.images.isEmpty())
    }

    @Test
    fun `when PropertyDetailDTO has multimedia then images contains their urls in order`() {
        val dto = propertyDetailDTO(
            multimedia = MultimediaDTO(
                images = listOf(
                    ImageDTO(url = "https://example.com/1.jpg", tag = null),
                    ImageDTO(url = "https://example.com/2.jpg", tag = null)
                )
            )
        )

        val result = dto.toDomain()

        assertEquals(listOf("https://example.com/1.jpg", "https://example.com/2.jpg"), result.images)
    }

    @Test
    fun `when PropertyDetailDTO has no moreCharacteristics then characteristics is null`() {
        val dto = propertyDetailDTO(moreCharacteristics = null)

        val result = dto.toDomain()

        assertNull(result.characteristics)
    }

    @Test
    fun `when PropertyDetailDTO has moreCharacteristics then characteristics maps every field`() {
        val moreCharacteristics = MoreCharacteristicsDTO(
            communityCosts = 50.0,
            roomNumber = 3,
            bathNumber = 2,
            exterior = true,
            housingFurnitures = null,
            agencyIsABank = false,
            energyCertificationType = null,
            flatLocation = null,
            modificationDate = null,
            constructedArea = 95,
            lift = true,
            boxroom = false,
            isDuplex = false,
            floor = "3",
            status = "good"
        )
        val dto = propertyDetailDTO(moreCharacteristics = moreCharacteristics)

        val result = dto.toDomain().characteristics

        assertEquals(moreCharacteristics.roomNumber, result?.rooms)
        assertEquals(moreCharacteristics.bathNumber, result?.bathrooms)
        assertEquals(moreCharacteristics.constructedArea, result?.constructedArea)
        assertEquals(moreCharacteristics.lift, result?.hasLift)
        assertEquals(moreCharacteristics.exterior, result?.isExterior)
        assertEquals(moreCharacteristics.status, result?.status)
        assertEquals(moreCharacteristics.floor, result?.floor)
    }

    @Test
    fun `when mapping a FavoriteEntity then returns a Favorite with the same fields`() {
        val entity = FavoriteEntity(propertyCode = "7", isFavorite = true, dateFavorited = 123456L)

        val result = entity.toDomain()

        assertEquals(entity.propertyCode, result.propertyCode)
        assertEquals(entity.dateFavorited, result.dateFavorited)
    }
}
