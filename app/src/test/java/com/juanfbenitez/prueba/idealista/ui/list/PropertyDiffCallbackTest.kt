package com.juanfbenitez.prueba.idealista.ui.list

import com.juanfbenitez.prueba.idealista.domain.model.Price
import com.juanfbenitez.prueba.idealista.domain.model.Property
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PropertyDiffCallbackTest {

    private val callback = PropertyAdapter.PropertyDiffCallback()

    private fun property(code: String = "1") = Property(
        propertyCode = code,
        thumbnail = "thumbnail",
        price = Price(amount = 100_000.0, currencySuffix = "€"),
        operation = PropertyOperation.SALE,
        propertyType = "flat",
        size = 90.0,
        rooms = 3,
        bathrooms = 2,
        address = "Calle Mayor, 1"
    )

    @Test
    fun `when items have same property code then areItemsTheSame returns true`() {
        val old = PropertyItemUiModel(property = property(code = "1"), isFavorite = false)
        val new = PropertyItemUiModel(property = property(code = "1"), isFavorite = true)

        assertTrue(callback.areItemsTheSame(old, new))
    }

    @Test
    fun `when items have different property code then areItemsTheSame returns false`() {
        val old = PropertyItemUiModel(property = property(code = "1"), isFavorite = false)
        val new = PropertyItemUiModel(property = property(code = "2"), isFavorite = false)

        assertFalse(callback.areItemsTheSame(old, new))
    }

    @Test
    fun `when items are fully equal then areContentsTheSame returns true`() {
        val old = PropertyItemUiModel(property = property(code = "1"), isFavorite = true, dateFavorited = 123L)
        val new = PropertyItemUiModel(property = property(code = "1"), isFavorite = true, dateFavorited = 123L)

        assertTrue(callback.areContentsTheSame(old, new))
    }

    @Test
    fun `when favorite state differs then areContentsTheSame returns false`() {
        val old = PropertyItemUiModel(property = property(code = "1"), isFavorite = false)
        val new = PropertyItemUiModel(property = property(code = "1"), isFavorite = true)

        assertFalse(callback.areContentsTheSame(old, new))
    }
}
