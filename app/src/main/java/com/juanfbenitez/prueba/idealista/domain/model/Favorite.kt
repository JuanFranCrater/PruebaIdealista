package com.juanfbenitez.prueba.idealista.domain.model

/** A property the user has marked as favorite, and when. */
data class Favorite(
    val propertyCode: String,
    val dateFavorited: Long
)
