package com.juanfbenitez.prueba.idealista.domain.model

/** Plain business representation of a property's full detail. */
data class PropertyDetail(
    val price: Price,
    val propertyType: String,
    val description: String?,
    val images: List<String>,
    val characteristics: PropertyCharacteristics?
)

/** Subset of a property's characteristics relevant to the detail screen. */
data class PropertyCharacteristics(
    val rooms: Int?,
    val bathrooms: Int?,
    val constructedArea: Int?,
    val hasLift: Boolean?,
    val isExterior: Boolean?,
    val status: String?,
    val floor: String?
)
