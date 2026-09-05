package com.juanfbenitez.prueba.idealista.data.mapper

import com.juanfbenitez.prueba.idealista.data.api.model.MoreCharacteristicsDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDTO
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import com.juanfbenitez.prueba.idealista.domain.model.Favorite
import com.juanfbenitez.prueba.idealista.domain.model.Price
import com.juanfbenitez.prueba.idealista.domain.model.Property
import com.juanfbenitez.prueba.idealista.domain.model.PropertyCharacteristics
import com.juanfbenitez.prueba.idealista.domain.model.PropertyDetail

/** Translates data-layer types (network DTOs, Room entities) into plain domain models, keeping
 * those framework-specific representations from leaking into the domain/use case and UI layers. */

fun PropertyDTO.toDomain(): Property = Property(
    propertyCode = propertyCode,
    thumbnail = thumbnail,
    price = Price(amount = priceInfo.price.amount, currencySuffix = priceInfo.price.currencySuffix),
    operation = operation,
    propertyType = propertyType,
    size = size,
    rooms = rooms,
    bathrooms = bathrooms,
    address = address
)

fun PropertyDetailDTO.toDomain(): PropertyDetail = PropertyDetail(
    price = Price(amount = price, currencySuffix = priceInfo.currencySuffix),
    propertyType = propertyType,
    description = propertyComment,
    images = multimedia?.images.orEmpty().map { it.url },
    characteristics = moreCharacteristics?.toDomain()
)

private fun MoreCharacteristicsDTO.toDomain(): PropertyCharacteristics = PropertyCharacteristics(
    rooms = roomNumber,
    bathrooms = bathNumber,
    constructedArea = constructedArea,
    hasLift = lift,
    isExterior = exterior,
    status = status,
    floor = floor
)

fun FavoriteEntity.toDomain(): Favorite = Favorite(
    propertyCode = propertyCode,
    dateFavorited = dateFavorited
)
