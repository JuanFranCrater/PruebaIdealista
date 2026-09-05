package com.juanfbenitez.prueba.idealista.domain.model

/** Plain business representation of a listing shown in the property list, independent of how
 * it was fetched (network DTO) or persisted. */
data class Property(
    val propertyCode: String,
    val thumbnail: String?,
    val price: Price,
    val operation: String,
    val propertyType: String,
    val size: Double,
    val rooms: Int,
    val bathrooms: Int,
    val address: String
)

/** A monetary amount together with the currency label used to display it. */
data class Price(
    val amount: Double,
    val currencySuffix: String
)
