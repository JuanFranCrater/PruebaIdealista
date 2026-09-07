package com.juanfbenitez.prueba.idealista.data.prefs

import org.junit.Assert.assertEquals
import org.junit.Test

class DetailDisplayModeTest {

    @Test
    fun `when pref value is drawer then fromPrefValue returns DRAWER`() {
        assertEquals(DetailDisplayMode.DRAWER, DetailDisplayMode.fromPrefValue("drawer"))
    }

    @Test
    fun `when pref value is full_screen then fromPrefValue returns FULL_SCREEN`() {
        assertEquals(DetailDisplayMode.FULL_SCREEN, DetailDisplayMode.fromPrefValue("full_screen"))
    }

    @Test
    fun `when pref value is null then fromPrefValue returns the default mode`() {
        assertEquals(DetailDisplayMode.Default, DetailDisplayMode.fromPrefValue(null))
    }

    @Test
    fun `when pref value is unrecognized then fromPrefValue returns the default mode`() {
        assertEquals(DetailDisplayMode.Default, DetailDisplayMode.fromPrefValue("unknown"))
    }
}
