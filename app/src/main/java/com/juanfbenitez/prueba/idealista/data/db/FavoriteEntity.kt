package com.juanfbenitez.prueba.idealista.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val propertyCode: String,
    val isFavorite: Boolean,
    val dateFavorited: Long // Unix timestamp
)
